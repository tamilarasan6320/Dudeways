package com.app.dudeways.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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

        typingStatusReference = firebaseDatabase.getReference("typing_status/$senderId")

        Glide.with(this)
            .load(session.getData("reciver_profile"))
            .placeholder(R.drawable.placeholder_bg)
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

        sentTone = soundPool.load(this, R.raw.recive_tone, 1)
        receiveTone = soundPool.load(this, R.raw.recive_tone, 1)

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

        binding.messageEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                typingStatusReference.setValue(s.toString().isNotEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        observeTypingStatus()
    }

    override fun onResume() {
        super.onResume()
        senderName?.let { sName ->
            receiverName?.let { rName ->
                fetchMessages(sName, rName, this)
            } ?: logError(CHATS_ACTIVITY, "Unable to fetch messages.")
        } ?: logError(CHATS_ACTIVITY, "Unable to fetch messages.")
    }

    private fun observeTypingStatus() {
        firebaseDatabase.getReference("typing_status/$receiverId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isTyping = snapshot.getValue(Boolean::class.java) ?: false
                    binding.typingStatus.visibility = if (isTyping) View.VISIBLE else View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    logError(CHATS_ACTIVITY, "Error observing typing status: ${error.message}")
                }
            })
    }

    private fun fetchMessages(
        senderName: String,
        receiverName: String,
        onMessagesFetchedListener: OnMessagesFetchedListener
    ) {
        val conversations: MutableList<ChatModel?> = mutableListOf()
        val reference = databaseReference.child("CHATS_V2").child(senderName).child(receiverName)

        reference.get().addOnSuccessListener { dataSnapshot ->
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
                onMessagesFetchedListener.onMessagesFetched(conversations)
            }
        }.addOnFailureListener { exception ->
            logError(CHATS_ACTIVITY, "Error fetching messages: ${exception.message}")
            onMessagesFetchedListener.onError(exception.message.toString())
        }

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatModel = snapshot.getValue(ChatModel::class.java)
                logInfo(CHATS_ACTIVITY, "onChildAdded - ChatModel: $chatModel")
                onMessagesFetchedListener.onMessageAdded(chatModel)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatModel = snapshot.getValue(ChatModel::class.java)
                logInfo(CHATS_ACTIVITY, "onChildChanged - ChatModel: $chatModel")
                onMessagesFetchedListener.onMessageChanged(chatModel)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val chatModel = snapshot.getValue(ChatModel::class.java)
                logInfo(CHATS_ACTIVITY, "onChildRemoved - ChatModel: $chatModel")
                onMessagesFetchedListener.onMessageRemoved(chatModel)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // No implementation needed
            }

            override fun onCancelled(error: DatabaseError) {
                logError(CHATS_ACTIVITY, "onCancelled - DatabaseError: ${error.message}")
            }
        })
    }

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
            dateTime = Timestamp.now().toDate().time.toString(),
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
                    updateMessagesForReceiver(senderID, receiverID, chatID, senderName, receiverName, message)
                    playSentTone()
                    logInfo(CHATS_ACTIVITY, "Message sent")
                } else {
                    logError(CHATS_ACTIVITY, "Failed to send message")
                }
            }
    }

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
            dateTime = Timestamp.now().toDate().time.toString(),
            message = message,
            msgSeen = true,
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

    private fun playSentTone() {
        soundPool.setOnLoadCompleteListener { _, _, _ -> }
        soundPool.play(sentTone, 1f, 1f, 0, 0, 1f)
    }

    private fun playReceiveTone() {
        soundPool.setOnLoadCompleteListener { _, _, _ -> }
        soundPool.play(receiveTone, 1f, 1f, 0, 0, 1f)
    }

    private var lastDisplayedDateTime: String? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessagesFetched(conversations: MutableList<ChatModel?>) {
        messages = conversations
        if (messages.isNotEmpty()) {
            messages.sortBy { it?.dateTime }
            initializeRecyclerView(messages)
            binding.RVChats.setOnScrollChangeListener { _, _, _, _, _ ->
                val layoutManager = binding.RVChats.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
                    val chatModel = chatAdapter?.getItemInfo(firstVisibleItemPosition)
                    val dateTime = chatModel?.dateTime

                    if (dateTime != lastDisplayedDateTime) {
                        lastDisplayedDateTime = dateTime
                        val actualDate = Date(dateTime?.toLong() ?: 0)
                        val todayDate = Date(Timestamp.now().toDate().time)
                        val formattedDate = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
                        binding.tvDate.visibility = View.VISIBLE
                        binding.tvDate.text = formattedDate.format(actualDate)
                    }
                }
            }
        } else {
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
                    messages.add(nonEmptyChatModel)
                    if (messages.any { it?.sentBy != session.getData(Constant.NAME) }) {
                        playReceiveTone()
                    }
                    messages.sortBy { it?.dateTime }
                    initializeRecyclerView(messages)
                    receiverName?.let { nonEmptyReceiverName ->
                        senderName?.let { nonEmptySenderName ->
                            nonEmptyChatModel.chatID?.let { nonEmptyChatID ->
                                updateMessageSeenStatus(
                                    receiverName = nonEmptyReceiverName,
                                    senderName = nonEmptySenderName,
                                    chatID = nonEmptyChatID
                                )
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

    override fun onMessageChanged(chatModel: ChatModel?) {
        logInfo(" $CHATS_ACTIVITY onMessageChanged", "Message changed: $chatModel")
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

    private fun initializeRecyclerView(conversations: MutableList<ChatModel?>) {
        chatAdapter = ChatAdapter(conversations, onClick = {}, session)
        binding.RVChats.apply {
            layoutManager = LinearLayoutManager(this@ChatsActivity)
            adapter = chatAdapter
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

}

private const val CHATS_ACTIVITY = "ChatsActivity"
