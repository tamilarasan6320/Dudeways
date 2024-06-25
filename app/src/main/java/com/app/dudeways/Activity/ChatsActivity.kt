package com.app.dudeways.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.dudeways.Adapter.ChatAdapter
import com.app.dudeways.Model.ChatModel
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityChatsBinding
import com.app.dudeways.extentions.logError
import com.app.dudeways.extentions.logInfo
import com.app.dudeways.extentions.makeToast
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.app.dudeways.listeners.OnMessagesFetchedListener
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class ChatsActivity : AppCompatActivity(), OnMessagesFetchedListener {

    lateinit var binding: ActivityChatsBinding
    lateinit var activity: Activity
    lateinit var session: Session
    private val firebaseDatabase: FirebaseDatabase =
        Firebase.database("https://dudeways-c8f31-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val databaseReference: DatabaseReference = firebaseDatabase.reference
    private var chatReference: DatabaseReference? = null
    private var chatAdapter: ChatAdapter? = null
    private var messages = mutableListOf<ChatModel?>()

    private var senderId = ""
    private var receiverId = ""
    private var senderName: String? = null
    private var receiverName: String? = null

    private lateinit var soundPool: SoundPool
    private var sentTone: Int = 0
    private var receiveTone: Int = 0

    private var isConversationsFetching: Boolean = true

    private lateinit var typingStatusReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        senderId = session.getData(Constant.USER_ID)
        receiverId = intent.getStringExtra("chat_user_id").toString()
        senderName = session.getData(Constant.NAME)
        receiverName = intent.getStringExtra("name")
        binding.tvName.text = receiverName
        chatReference =
            databaseReference.child("CHATS_V2").child(senderName!!).child(receiverName!!)
        typingStatusReference = firebaseDatabase.getReference("typing_status/$senderId")

        Glide.with(this)
            .load(session.getData("reciver_profile"))
            .placeholder(R.drawable.profile_placeholder)
            .into(binding.ivProfile)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        sentTone = soundPool.load(this, R.raw.receive_tone, 1)
        receiveTone = soundPool.load(this, R.raw.sent_tone, 1)

        binding.sendButton.setOnClickListener {
            val message = binding.messageEdittext.text.toString()
            if (message.isNotEmpty()) {
                senderName?.let { sName ->
                    receiverName?.let { rName ->
                        updateMessagesForSender(senderId, receiverId, sName, rName, message)
                        binding.messageEdittext.text.clear()
                    } ?: logError(CHATS_ACTIVITY, "Unable to send your message.")
                } ?: logError(CHATS_ACTIVITY, "Unable to send your message.")
            } else {
                makeToast("Enter text to send")
            }
        }

        // Inside your onCreate method
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) { // keyboard is opened
                binding.RVChats.smoothScrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
            }
        }

        binding.messageEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                typingStatusReference.setValue(s.toString().isNotEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.ivMore.setOnClickListener {
            showPopupMenu()
        }

        fetchMessages(chatReference, this@ChatsActivity)
        chatReference?.addChildEventListener(
            object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatModel = snapshot.getValue(ChatModel::class.java)
                    logInfo(CHATS_ACTIVITY, "from firebase child added - $chatModel")
                    onMessageAdded(chatModel)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                    val chatModel = snapshot.getValue(ChatModel::class.java)
//                    logInfo(CHATS_ACTIVITY, "Child changed - $chatModel")
//                    onMessageChanged(chatModel)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val chatModel = snapshot.getValue(ChatModel::class.java)
                    onMessageRemoved(chatModel)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
        )


        observeTypingStatus()
        setUserStatus(true)
        observeUserStatus()
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, binding.ivMore)
        popupMenu.inflate(R.menu.chat_options_menu)

        // Check current block status
        isBlocked(senderId, receiverId) { blocked ->
            val blockMenuItem = popupMenu.menu.findItem(R.id.menu_block_chat)
            blockMenuItem.title = if (blocked) "Unblock User" else "Block User"

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_block_chat -> {
                        if (blocked) {
                            // Unblock user
                            updateBlockStatus(senderId, receiverId, false)
                        } else {
                            // Block user
                            updateBlockStatus(senderId, receiverId, true)
                        }
                        true
                    }

                    R.id.menu_clear_chat -> {
                        // Implement clear chat functionality if needed
                        true
                    }

                    R.id.menu_report -> {
                        report()
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    // Function to update block status
    private fun updateBlockStatus(senderId: String, receiverId: String, blocked: Boolean) {
        val blockStatusRef = firebaseDatabase.getReference("block_status")

        // Update block status for sender -> receiver
        blockStatusRef.child(senderId).child(receiverId).setValue(blocked)
            .addOnSuccessListener {
                if (blocked) {
                    logInfo(CHATS_ACTIVITY, "User blocked successfully")
                } else {
                    logInfo(CHATS_ACTIVITY, "User unblocked successfully")
                }
            }
            .addOnFailureListener { exception ->
                logError(CHATS_ACTIVITY, "Failed to update block status: ${exception.message}")
            }

        // Update block status for receiver -> sender (optional)
        blockStatusRef.child(receiverId).child(senderId).setValue(blocked)
            .addOnSuccessListener {
                if (blocked) {
                    logInfo(CHATS_ACTIVITY, "User blocked successfully (reverse)")
                } else {
                    logInfo(CHATS_ACTIVITY, "User unblocked successfully (reverse)")
                }
            }
            .addOnFailureListener { exception ->
                logError(
                    CHATS_ACTIVITY,
                    "Failed to update block status (reverse): ${exception.message}"
                )
            }
    }

    // Function to check if user is blocked
    private fun isBlocked(senderId: String, receiverId: String, callback: (Boolean) -> Unit) {
        val blockStatusRef = firebaseDatabase.getReference("block_status")

        blockStatusRef.child(senderId).child(receiverId).get()
            .addOnSuccessListener { snapshot ->
                val isBlocked = snapshot.getValue(Boolean::class.java) ?: false
                callback(isBlocked)
            }
            .addOnFailureListener { exception ->
                logError(CHATS_ACTIVITY, "Error checking block status: ${exception.message}")
                callback(false) // Default to not blocked in case of error
            }
    }


    private fun report() {
        // Implement report functionality
    }

    private fun clearLocalMessages() {
        messages.clear()
        chatAdapter?.notifyDataSetChanged()
    }

    private fun clearChatInFirebase(senderName: String, receiverName: String) {
        // Reference to sender's chat with receiver
        val senderChatRef =
            databaseReference.child("CHATS_V2").child(senderName).child(receiverName)
        senderChatRef.removeValue()
            .addOnSuccessListener {
                logInfo(CHATS_ACTIVITY, "Chat cleared successfully for sender")
            }
            .addOnFailureListener { exception ->
                logError(CHATS_ACTIVITY, "Failed to clear chat for sender: ${exception.message}")
            }

        // Reference to receiver's chat with sender
        val receiverChatRef =
            databaseReference.child("CHATS_V2").child(receiverName).child(senderName)
        receiverChatRef.removeValue()
            .addOnSuccessListener {
                logInfo(CHATS_ACTIVITY, "Chat cleared successfully for receiver")
            }
            .addOnFailureListener { exception ->
                logError(CHATS_ACTIVITY, "Failed to clear chat for receiver: ${exception.message}")
            }
    }


    /**
     *  Set user status and updates to firebase
     */
    private fun setUserStatus(isOnline: Boolean) {
        val userStatusRef = firebaseDatabase.getReference("user_status/$senderId")

        val status = if (isOnline) {
            mapOf("status" to "online")
        } else {
            mapOf("status" to "offline", "last_seen" to System.currentTimeMillis())
        }

        userStatusRef.setValue(status).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logInfo(CHATS_ACTIVITY, "User status updated: $status")
            } else {
                logError(CHATS_ACTIVITY, "Failed to update user status: ${task.exception?.message}")
            }
        }
    }


    /**
     *  Observes typing status and updates the UI accordingly.
     */
    private fun observeTypingStatus() {
        firebaseDatabase.getReference("typing_status/$receiverId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isTyping = snapshot.getValue(Boolean::class.java) ?: false
                    binding.typingStatus.visibility = if (isTyping) View.VISIBLE else View.GONE
                    binding.tvLastSeen.visibility = if (isTyping) View.GONE else View.VISIBLE
                }

                override fun onCancelled(error: DatabaseError) {
                    logError(CHATS_ACTIVITY, "Error observing typing status: ${error.message}")
                }
            })
    }

    /**
     *  Observes user status and updates the UI accordingly.
     */
    private fun observeUserStatus() {
        val userStatusRef = firebaseDatabase.getReference("user_status/$receiverId")

        userStatusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.child("status").getValue(String::class.java)
                val lastSeen = snapshot.child("last_seen").getValue(Long::class.java) ?: 0L

                if (status == "online") {
                    binding.tvLastSeen.text = "In chat"
                    binding.IVOnlineStatus.visibility = View.VISIBLE
                } else {
                    binding.IVOnlineStatus.visibility = View.GONE
                    val currentTimeMillis = System.currentTimeMillis()
                    val timeAgoMillis = currentTimeMillis - lastSeen

                    val timeAgo: CharSequence = if (timeAgoMillis < DateUtils.DAY_IN_MILLIS) {
                        DateUtils.getRelativeTimeSpanString(
                            lastSeen,
                            currentTimeMillis,
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_RELATIVE
                        )
                    } else {
                        val daysAgo = (timeAgoMillis / DateUtils.DAY_IN_MILLIS).toInt()
                        when (daysAgo) {
                            1 -> "1 day ago"
                            else -> "$daysAgo days ago"
                        }
                    }

                    binding.tvLastSeen.text = timeAgo
                }
            }

            override fun onCancelled(error: DatabaseError) {
                logError(CHATS_ACTIVITY, "Failed to fetch user status: ${error.message}")
            }
        })
    }

    /**
     *  Fetch messages from Firebase
     */
    private fun fetchMessages(
        chatReference: DatabaseReference?,
        onMessagesFetchedListener: OnMessagesFetchedListener
    ) {
        val conversations: MutableList<ChatModel?> = mutableListOf()
        chatReference?.get()?.addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                for (child in dataSnapshot.children) {
                    val chatModel = child.getValue(ChatModel::class.java)
                    if (chatModel != null) {
                        logInfo(CHATS_ACTIVITY, "ChatModel: $chatModel")
                        conversations.add(chatModel)
                    } else {
                        logError(CHATS_ACTIVITY, "Unable to load your conversations.")
                    }
                }
                onMessagesFetchedListener.onMessagesFetched(conversations)
                isConversationsFetching = false
            } else {
                logInfo(CHATS_ACTIVITY, "No messages found.")
            }
        }?.addOnFailureListener { exception ->
            logError(CHATS_ACTIVITY, "Error fetching messages: ${exception.message}")
            onMessagesFetchedListener.onError(exception.message.toString())
        }
    }

    /**
     *  Update the message for sender in firebase.
     */
    private fun updateMessagesForSender(
        senderID: String,
        receiverID: String,
        senderName: String,
        receiverName: String,
        message: String
    ) {
        val chatID = Random.nextInt(100000, 999999).toString()
        val chatModel = ChatModel(
            attachmentType = "TEXT",
            chatID = chatID,
            dateTime = Timestamp.now().toDate().time,
            message = message,
            msgSeen = false,
            receiverID = receiverID,
            senderID = senderID,
            type = "TEXT",
            sentBy = session.getData(Constant.NAME)
        )
        databaseReference.child("CHATS_V2")
            .child(senderName)
            .child(receiverName)
            .child(chatID)
            .setValue(chatModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addChat(message)
                    updateMessagesForReceiver(
                        senderID,
                        receiverID,
                        chatID,
                        senderName,
                        receiverName,
                        message
                    )
                    playSentTone()
                    logInfo(CHATS_ACTIVITY, "Message sent")
                } else {
                    logError(CHATS_ACTIVITY, "Failed to send message")
                }
            }
    }

    /**
     *  Update the message for receiver in firebase.
     */
    private fun updateMessagesForReceiver(
        senderID: String,
        receiverID: String,
        chatID: String,
        senderName: String,
        receiverName: String,
        message: String
    ) {
        val chatModel = ChatModel(
            attachmentType = "TEXT",
            chatID = chatID,
            dateTime = Timestamp.now().toDate().time,
            message = message,
            msgSeen = false,
            receiverID = senderID,
            senderID = receiverID,
            type = "TEXT",
            sentBy = session.getData(Constant.NAME)
        )
        databaseReference.child("CHATS_V2")
            .child(receiverName)
            .child(senderName)
            .child(chatID)
            .setValue(chatModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    logInfo("updateMessages", "Message updated for receiver")
                } else {
                    logError("updateMessages", "Failed to send message: ${task.exception?.message}")
                }
            }
    }

    /**
     *  Play sent tone
     */
    private fun playSentTone() {
        logInfo(CHATS_ACTIVITY, "Attempting to play sent tone")
        val result = soundPool.play(sentTone, 1f, 1f, 0, 0, 1f)
        if (result == 0) {
            Toast.makeText(this, "Failed to play sent tone", Toast.LENGTH_SHORT).show()
            logError(CHATS_ACTIVITY, "Failed to play sent tone")
        } else {
            Toast.makeText(this, "Sent tone played successfully", Toast.LENGTH_SHORT).show()
            logInfo(CHATS_ACTIVITY, "Sent tone played successfully")
        }
    }

    /**
     *  Play receive tone
     */
    private fun playReceiveTone() {
        logInfo(CHATS_ACTIVITY, "Attempting to play receive tone")
        val result = soundPool.play(receiveTone, 1f, 1f, 0, 0, 1f)
        if (result == 0) {
            logError(CHATS_ACTIVITY, "Failed to play receive tone")
        } else {
            logInfo(CHATS_ACTIVITY, "Receive tone played successfully")
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onMessagesFetched(conversations: MutableList<ChatModel?>) {
        var lastDisplayedDateTime: Long? = null
        messages = conversations
        if (messages.isNotEmpty()) {
            messages.sortBy { it?.dateTime }
            initializeRecyclerView(messages)
            binding.RVChats.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
                        val chatModel = chatAdapter?.getItemInfo(firstVisibleItemPosition)
                        val dateTime = chatModel?.dateTime

                        if (dateTime != lastDisplayedDateTime) {
                            lastDisplayedDateTime = dateTime
                            val actualDate = dateTime?.let { Date(it) }
                            val formattedDate = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
                            binding.tvDate.visibility = View.VISIBLE
                            binding.tvDate.text = actualDate?.let { formattedDate.format(it) }
                        }
                    }
                }
            })
            binding.RVChats.scrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
        } else {
            //Display empty conversation placeholder.
            logInfo("conversations", "Conversations are empty.")
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageAdded(chatModel: ChatModel?) {
        chatModel?.let { nonEmptyChatModel ->
            when {
                messages.isEmpty() -> {
                    logInfo("$CHATS_ACTIVITY onMessageAdded", "Message added are empty")
                    messages.add(nonEmptyChatModel)
                    initializeRecyclerView(messages)
                    if (messages.any { it?.sentBy != session.getData(Constant.NAME) }) {
                        playReceiveTone()
                    } else {

                    }
                }

                messages.none { existingChatModel -> existingChatModel?.chatID == chatModel.chatID } -> {
                    Log
                        .e("ADDED_NON_EMPTY_CHAT_MODEL", nonEmptyChatModel.toString())
                    messages.add(nonEmptyChatModel)
                    if (messages.any { it?.sentBy != session.getData(Constant.NAME) }) {
                        playReceiveTone()
                    }
                    messages.sortBy { it?.dateTime }
                    initializeRecyclerView(messages)
                    receiverName?.let { nonEmptyReceiverName ->
                        senderName?.let { nonEmptySenderName ->
                            nonEmptyChatModel.chatID?.let { nonEmptyChatID ->
                                //Todo : This causing the bug in the app. Fix this to enable tick functionality
//                                updateMessageSeenStatus(
//                                    receiverName = nonEmptyReceiverName,
//                                    senderName = nonEmptySenderName,
//                                    chatID = nonEmptyChatID
//                                )
                            }
                        }
                    }
                    binding.RVChats.smoothScrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
                    logInfo("$CHATS_ACTIVITY onMessageAdded", "Message added: $chatModel")
                }

                else -> {
                    logInfo("$CHATS_ACTIVITY onMessageAdded", "Duplicate message ignored")
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageChanged(chatModel: ChatModel?) {
        logInfo(CHATS_ACTIVITY, "Message changed - $chatModel")
        chatAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageRemoved(chatModel: ChatModel?) {
        messages.remove(chatModel)
        chatAdapter?.notifyDataSetChanged()
        logInfo("$CHATS_ACTIVITY onMessageRemoved", "Message removed: $chatModel")
    }

    override fun onError(errorMessage: String) {
        makeToast("Error: $errorMessage")
        logError("$CHATS_ACTIVITY onError", "Error: $errorMessage")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initializeRecyclerView(conversations: MutableList<ChatModel?>) {
        chatAdapter = ChatAdapter(conversations, onClick = {}, session)
        chatAdapter?.notifyDataSetChanged()
        binding.RVChats.apply {
            layoutManager = LinearLayoutManager(this@ChatsActivity)
            adapter = chatAdapter
            scrollToPosition(
                chatAdapter?.itemCount?.minus(1) ?: 0
            ) // Ensure scrolling to the last message
            invalidate()
        }
    }


    private fun addChat(message: String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.CHAT_USER_ID] = receiverId
        params[Constant.MESSAGE] = message
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        logInfo(CHATS_ACTIVITY, "Message update to the API.")
                    } else {
                        logError(CHATS_ACTIVITY, "Message failed to update to the API.")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.ADD_CHAT, params, false, 1)
    }


    private fun updateMessageSeenStatus(
        receiverName: String,
        senderName: String,
        chatID: String
    ) {
        databaseReference.child("CHATS_V2")
            .child(receiverName)
            .child(senderName)
            .child(chatID)
            .updateChildren(mapOf("msgSeen" to true))
            .addOnSuccessListener {
                logInfo(CHATS_ACTIVITY, "Message seen status updated for chat ID: $chatID")
            }
            .addOnFailureListener { exception ->
                logError(CHATS_ACTIVITY, "Error updating message seen status: ${exception.message}")
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Set user offline status with last seen time
        setUserStatus(false)
    }


}

private const val CHATS_ACTIVITY = "ChatsActivity"
