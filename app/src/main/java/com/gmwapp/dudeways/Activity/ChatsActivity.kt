package com.gmwapp.dudeways.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmwapp.dudeways.Adapter.ChatAdapter
import com.gmwapp.dudeways.Model.ChatModel
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityChatsBinding
import com.gmwapp.dudeways.extentions.fetchMessages
import com.gmwapp.dudeways.extentions.logError
import com.gmwapp.dudeways.extentions.logInfo
import com.gmwapp.dudeways.extentions.observeUserStatus
import com.gmwapp.dudeways.extentions.playReceiveTone
import com.gmwapp.dudeways.extentions.updateMessagesForSender
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.gmwapp.dudeways.listeners.OnMessagesFetchedListener
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.extentions.chat_status
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class ChatsActivity : BaseActivity(), OnMessagesFetchedListener {
    lateinit var binding: ActivityChatsBinding
    lateinit var activity: Activity
    lateinit var session: Session
    private val firebaseDatabase: FirebaseDatabase = Firebase.database("https://dudeways-c8f31-default-rtdb.asia-southeast1.firebasedatabase.app")
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
    private var friend_verified = ""
    private var isConversationsFetching: Boolean = true
    private lateinit var typingStatusReference: DatabaseReference
    private var lastMessageId: String? = null


    var gender = ""
    var  verified = ""






    private fun handleDeepLink(intent: Intent?) {
        val action = intent?.action
        val data: Uri? = intent?.data

        if (Intent.ACTION_VIEW == action && data != null) {
            // Handle the deep link
            val userId = data.getQueryParameter("userid")
            val chatId = data.getQueryParameter("chatid")
//            val linkFriendVerified = data.getQueryParameter("friend_verified")
//            val linkSenderName = data.getQueryParameter("senderName")
//            val linkReceiverName = data.getQueryParameter("receiverName")


            if (userId != null && chatId != null) {
                // Display the extracted user ID and chat ID in a toast message
              //  Toast.makeText(this, "User ID: $userId, Chat ID: $chatId", Toast.LENGTH_SHORT).show()

                other_userdetails(userId,chatId)


                // You can now use these variables within the current activity as needed
            } else {
                Toast.makeText(this, "Missing one or more query parameters", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle the case when the intent does not contain a deep link
            // Get data from the intent extras
            senderId = session.getData(Constant.USER_ID)
            receiverId = intent?.getStringExtra("chat_user_id").toString()
            friend_verified = intent?.getStringExtra("friend_verified").toString()
            senderName = session.getData(Constant.UNIQUE_NAME)
            receiverName = intent?.getStringExtra("unique_name").toString()
            binding.tvName.text = intent!!.getStringExtra("name")
            read_chats()


            chatReference = databaseReference.child("CHATS_V2").child(senderName!!).child(receiverName!!)
            typingStatusReference = firebaseDatabase.getReference("typing_status/$senderId")
            // You can now use these variables or proceed to open a new activity if needed


            gender = session.getData(Constant.GENDER)
            verified = session.getData(Constant.VERIFIED)


            if (friend_verified == "1") {
                binding.tvAbout.visibility = View.VISIBLE
                binding.ivVerified.visibility = View.VISIBLE
            } else {
                binding.tvAbout.visibility = View.VISIBLE
                binding.ivVerified.visibility = View.GONE
            }

            Glide.with(this)
                .load(session.getData("reciver_profile"))
                .placeholder(R.drawable.profile_placeholder)
                .into(binding.ivProfile)

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        handleDeepLink(intent)

//
//        senderId = session.getData(Constant.USER_ID)
//        receiverId = intent.getStringExtra("chat_user_id").toString()
//        friend_verified = intent.getStringExtra("friend_verified").toString()
//        senderName = session.getData(Constant.UNIQUE_NAME)
//        receiverName = intent.getStringExtra("unique_name")
//        binding.tvName.text = intent.getStringExtra("name")
//        read_chats()
//        chatReference = databaseReference.child("CHATS_V2").child(senderName!!).child(receiverName!!)
//        typingStatusReference = firebaseDatabase.getReference("typing_status/$senderId")
//
//        gender = session.getData(Constant.GENDER)
//        verified = session.getData(Constant.VERIFIED)
//
//
//        if (friend_verified == "1") {
//            binding.tvAbout.visibility = View.VISIBLE
//            binding.ivVerified.visibility = View.VISIBLE
//        } else {
//            binding.tvAbout.visibility = View.VISIBLE
//            binding.ivVerified.visibility = View.GONE
//        }





    }

    override fun onResume() {
        super.onResume()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivProfile.setOnClickListener{
            val intent = Intent(activity, ProfileinfoActivity::class.java)
            intent.putExtra("chat_user_id", receiverId!!)
            activity.startActivity(intent)
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        sentTone = soundPool.load(this, R.raw.sent_new, 1)
        //  receiveTone = soundPool.load(this, R.raw.recieve_tone, 1)

        binding.sendButton.setOnClickListener {
            //disable binding.sendButton.isClickable = false

            if (gender == "male" && verified == "0") {
                showCustomDialog(this)
            }
            else {
                binding.sendButton.isClickable = false
                val params: MutableMap<String, String> = HashMap()
                params[Constant.USER_ID] = session.getData(Constant.USER_ID)
                params[Constant.CHAT_USER_ID] = receiverId
                params[Constant.UNREAD] = "1"
                params[Constant.MSG_SEEN] = "1"
                params[Constant.MESSAGE] = binding.messageEdittext.text.toString()
                ApiConfig.RequestToVolley({ result, response ->
                    if (result) {
                        try {
                            val jsonObject = JSONObject(response)
                            if (jsonObject.getBoolean(Constant.SUCCESS)) {
                                chat_status = jsonObject.getString("chat_status")
                                session.setData(Constant.CHAT_STATUS, chat_status)
                                session.setData(Constant.MSG_SEEN, "1")
                                val message = binding.messageEdittext.text.toString()
                                if (message.isNotEmpty()) {
                                    isBlocked(senderId, receiverId) { isBlocked ->
                                        if (isBlocked) {
                                            binding.sendButton.isClickable = true
                                            makeToast("You cannot send messages to this user blocked.")
                                        } else {
                                            binding.sendButton.isClickable = true

                                            senderName?.let { sName ->
                                                receiverName?.let { rName ->
                                                    updateMessagesForSender(
                                                        databaseReference = databaseReference,
                                                        senderID = senderId,
                                                        receiverID = receiverId,
                                                        senderName = senderName!!,
                                                        receiverName = receiverName!!,
                                                        message = message,
                                                        soundPool = soundPool,
                                                        sentTone = sentTone
                                                    )
                                                    binding.messageEdittext.text.clear()
                                                } ?: logError(
                                                    CHATS_ACTIVITY,
                                                    "Unable to send your message."
                                                )
                                            } ?: logError(
                                                CHATS_ACTIVITY,
                                                "Unable to send your message."
                                            )
                                        }
                                    }
                                } else {
                                    binding.sendButton.isClickable = true
                                    makeToast("Enter text to send")
                                }

                                //   Toast.makeText(this, chat_status, Toast.LENGTH_SHORT).show()


                            } else {
                                binding.sendButton.isClickable = true

                                chat_status = jsonObject.getString("chat_status")
                                session.setData(Constant.CHAT_STATUS, chat_status)


                                val dialogView =
                                    activity.layoutInflater.inflate(R.layout.dialog_custom, null)

                                val dialogBuilder = AlertDialog.Builder(activity)
                                    .setView(dialogView)
                                    .create()
                                val title = dialogView.findViewById<TextView>(R.id.dialog_title)
                                val btnPurchase =
                                    dialogView.findViewById<LinearLayout>(R.id.btnPurchase)
                                val btnFreePoints =
                                    dialogView.findViewById<LinearLayout>(R.id.btnFreePoints)

                                val tv_how_points = dialogView.findViewById<TextView>(R.id.tv_how_points)

                                tv_how_points.visibility = View.GONE


                                title.text = "You have ${session.getData(Constant.POINTS)} Points"

                                btnPurchase.setOnClickListener {
                                    val intent = Intent(activity, PurchasepointActivity::class.java)
                                    activity.startActivity(intent)
                                    dialogBuilder.dismiss()
                                }

                                btnFreePoints.setOnClickListener {
                                    val intent = Intent(activity, FreePointsActivity::class.java)
                                    activity.startActivity(intent)
                                    dialogBuilder.dismiss()
                                }


                                dialogBuilder.show()

                                //    Toast.makeText(this, chat_status, Toast.LENGTH_SHORT).show()

                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }, activity, Constant.ADD_CHAT, params, false, 1)
            }

        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) { // keyboard is opened
                binding.RVChats.postDelayed({
                    //here
                    //     binding.RVChats.smoothScrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
                }, 100)
            }
        }

        binding.messageEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                typingStatusReference = firebaseDatabase.getReference("typing_status/${senderId}_to_${receiverId}")
                typingStatusReference.setValue(s.toString().isNotEmpty())
            }


            override fun afterTextChanged(s: Editable?) {}
        })


        binding.ivMore.setOnClickListener {
            showPopupMenu()
        }

        fetchMessages(chatReference, this@ChatsActivity) {
            isConversationsFetching = it
        }

        chatReference?.addChildEventListener(
            object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatModel = snapshot.getValue(ChatModel::class.java)
                    logInfo(CHATS_ACTIVITY, "from firebase child added - $chatModel")
                    onMessageAdded(chatModel)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val chatModel = snapshot.getValue(ChatModel::class.java)
                    onMessageRemoved(chatModel)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // Not implemented
                }

                override fun onCancelled(error: DatabaseError) {
                    // Not implemented
                }
            }
        )

        observeTypingStatus(firebaseDatabase, receiverId)
        setUserStatus(firebaseDatabase, senderId, true)
        observeUserStatus(firebaseDatabase, receiverId)


    }

    private fun ChatsActivity.observeTypingStatus(
        firebaseDatabase: FirebaseDatabase,
        receiverID: String,
    ) {
        val typingStatusReference = firebaseDatabase.getReference("typing_status/${receiverID}_to_${senderId}")
        typingStatusReference.addValueEventListener(object : ValueEventListener {
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

    fun observeMessageSeenStatus() {
        val seenStatusReference = firebaseDatabase.getReference("CHATS_V2/$receiverName/$senderName")
        seenStatusReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("SeenStatus", "onChildAdded: ${snapshot.key}")
                updateSeenStatus(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("SeenStatus", "onChildChanged: ${snapshot.key}")
                updateSeenStatus(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Not needed for seen status
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Not needed for seen status
            }

            override fun onCancelled(error: DatabaseError) {
                logError("CHATS_ACTIVITY", "Error observing message seen status: ${error.message}")
            }
        })
    }

    private fun updateSeenStatus(snapshot: DataSnapshot) {
        val chatModel = snapshot.getValue(ChatModel::class.java)
        chatModel?.let {
            Log.d("SeenStatus", "Processing message: ${it.message}, ID: ${snapshot.key}, ReceiverID: ${it.receiverID}, SenderID: $senderId, MsgSeen: ${it.msgSeen}")

            // Check if this is the most recent message
            if (it.receiverID == senderId && it.msgSeen == true) {
                if (snapshot.key == lastMessageId) {
                    Log.d("SeenStatus", "Last message seen by receiver: ${it.message}")
                    makeToast("Your message has been seen.")
                } else {
                    Log.d("SeenStatus", "Message is not the last one.")
                }
            }

            // Update lastMessageId with the current message ID
            lastMessageId = snapshot.key
        } ?: Log.d("SeenStatus", "ChatModel is null for snapshot: ${snapshot.key}")
    }

    private fun makeToast(message: String) {
        // Ensure to call this on the main thread if it's not already
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onBackPressed() {
        read_chats()
        // Set typing status to false
        onStop()


        // Call the super method to handle the back press action
        super.onBackPressed()
    }


    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, binding.ivMore)
        popupMenu.inflate(R.menu.chat_options_menu)

        isBlocked(senderId, receiverId) { blocked ->
            val blockMenuItem = popupMenu.menu.findItem(R.id.menu_block_chat)
            blockMenuItem.title = if (blocked) "Unblock User" else "Block User"

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_block_chat -> {
                        updateBlockStatus(senderId, receiverId, !blocked)
                        true
                    }
                    R.id.menu_clear_chat -> {
                        clearChatInFirebase(senderName ?: "", receiverName ?: "", receiverId)
                        true
                    }
                    R.id.menu_report -> {
                        addFriend()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun addFriend() {
        val session = Session(activity)
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.FRIEND_USER_ID] = receiverId!!
        params[Constant.FRIEND] = "2"
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.ADD_FRIENDS, params, true, 1)
    }
    fun msg_seen() {
        val session = Session(activity)
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.CHAT_USER_ID] = receiverId!!
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setData(Constant.MSG_SEEN, "1")
                        chatAdapter?.notifyDataSetChanged()
                        //Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                       // Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.MSG_SEEN_URL, params, false, 1)
    }

    fun read_chats() {
        val session = Session(activity)
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.CHAT_USER_ID] = receiverId!!
        params[Constant.MSG_SEEN] = "0"
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                    //   Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.READ_CHATS, params, false, 1)
    }

    private fun updateBlockStatus(senderId: String, receiverId: String, isBlocked: Boolean) {
        val blockReference = databaseReference.child("blocked_users").child(senderId).child(receiverId)
        blockReference.setValue(isBlocked).addOnSuccessListener {
            val message = if (isBlocked) {
                "User has been blocked"
            } else {
                "User has been unblocked"
            }
            makeToast(message)
        }.addOnFailureListener {
            makeToast("Failed to update block status")
        }
    }

    private fun isBlocked(senderId: String, receiverId: String, callback: (Boolean) -> Unit) {
        val blockReference = databaseReference.child("blocked_users").child(senderId).child(receiverId)
        blockReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isBlocked = snapshot.getValue(Boolean::class.java) ?: false
                callback(isBlocked)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    private fun clearChatInFirebase(senderName: String, receiverName: String, receiverID: String) {
        val senderChatReference = databaseReference.child("CHATS_V2").child(senderName).child(receiverName)
        val receiverChatReference = databaseReference.child("CHATS_V2").child(receiverName).child(senderName)

        senderChatReference.removeValue().addOnSuccessListener {
            deleteChat(receiverID)
            makeToast("Chat cleared successfully")
            messages.clear()
            chatAdapter?.notifyDataSetChanged()
        }.addOnFailureListener {
            makeToast("Failed to clear chat")
        }

        receiverChatReference.removeValue().addOnFailureListener {
            makeToast("Failed to clear chat for receiver")
        }
    }

    fun deleteChat(receiverID: String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.CHAT_USER_ID] = receiverID

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val  Intent = Intent(activity, HomeActivity::class.java)
                        startActivity(Intent)
                        finish()
                        makeToast(jsonObject.getString(Constant.MESSAGE))
                    } else {
                        makeToast(jsonObject.getString(Constant.MESSAGE))
                        // Do something if needed
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.DELETE_CHAT, params, true, 1)
    }



    @SuppressLint("NotifyDataSetChanged")
    override fun onMessagesFetched(conversations: MutableList<ChatModel?>) {
        var lastDisplayedDateTime: Long? = null
        messages = conversations
        if (messages.isNotEmpty()) {
            messages.sortBy { it?.dateTime }
            initializeRecyclerView(messages)

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

                        playReceiveTone(
                            soundPool,
                            receiveTone
                        )
                    } else {

                    }
                }

                messages.none { existingChatModel -> existingChatModel?.chatID == chatModel.chatID } -> {
                    Log
                        .e("ADDED_NON_EMPTY_CHAT_MODEL", nonEmptyChatModel.toString())
                    messages.add(nonEmptyChatModel)
                    if (messages.any { it?.sentBy != session.getData(Constant.NAME) }) {
                        playReceiveTone(
                            soundPool,
                            receiveTone
                        )
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
                    read_chats()
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
        chatAdapter = ChatAdapter(
            conversations,
            onClick = { /* Handle message click */ },
            session,
            onMessageDelete = { chatModel ->
                // Handle message deletion here, e.g., remove from database
                // You might need to implement logic to actually delete the message from your data source
                // For example:
                // chatViewModel.deleteMessage(chatModel)
            }
        )
        binding.RVChats.apply {
            layoutManager = LinearLayoutManager(this@ChatsActivity)
            adapter = chatAdapter
            scrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0) // Ensure scrolling to the last message
            invalidate()
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        // Set user offline status with last seen time
        setUserStatus(
            firebaseDatabase,
            senderId,
            false
        )
    }

    override fun onStop() {
        super.onStop()
        typingStatusReference = firebaseDatabase.getReference("typing_status/${senderId}_to_${receiverId}")
        typingStatusReference.setValue(false)
        setUserStatus(firebaseDatabase, senderId, false)
    }


    override fun onStart() {
        super.onStart()
        // Set user online status
        setUserStatus(firebaseDatabase, senderId, true)
    }

    fun showCustomDialog(context: Context) {
        // Inflate the custom layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)

        // Create an AlertDialog builder and set the custom layout
        val builder = AlertDialog.Builder(context)
            .setView(dialogView)

        // Create the dialog
        val dialog = builder.create()

        // Get references to the views in the custom layout





     val btnVerify = dialogView.findViewById<Button>(R.id.btnVerify)

        // Set up click listeners for the buttons
        btnVerify.setOnClickListener {

            val proof1 = session.getData(Constant.SELFIE_IMAGE)
            val proof2 = session.getData(Constant.FRONT_IMAGE)
            val proof3 = session.getData(Constant.BACK_IMAGE)
            val status = session.getData(Constant.STATUS)
            val payment_status = session.getData(Constant.PAYMENT_STATUS)


            // if proof 1 2 3 is empty
            if(proof1.isEmpty() || proof2.isEmpty() || proof3.isEmpty()) {
                val intent = Intent(activity, IdverficationActivity::class.java)
                startActivity(intent)
            }
            else if (payment_status == "0") {
                val intent = Intent(activity, PurchaseverifybuttonActivity::class.java)
                startActivity(intent)
            }
            else if (status == "0") {
                val intent = Intent(activity, Stage4Activity::class.java)
                startActivity(intent)
            }

            else if (status == "1"){
                val intent = Intent(activity, VerifiedActivity::class.java)
                startActivity(intent)
            }


            dialog.dismiss() // Dismiss the dialog
        }




        // Show the dialog
        dialog.show()
    }

    fun setUserStatus(
        firebaseDatabase: FirebaseDatabase,
        senderID: String,
        isOnline: Boolean
    ) {
        val userStatusRef = firebaseDatabase.getReference("user_status/$senderID")

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




    private fun other_userdetails(user_id: String?, chatId: String?) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = user_id!!
        params[Constant.OTHER_USER_ID] = chatId!!
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)



                        // Assign values to variables
                        senderId = user_id!!
                        receiverId = chatId!!
                        friend_verified = jsonobj.getString(Constant.VERIFIED).toString()
                        senderName = session.getData(Constant.UNIQUE_NAME).toString()
                        receiverName = jsonobj.getString(Constant.UNIQUE_NAME).toString()
                        binding.tvName.text = jsonobj.getString(Constant.NAME).toString()
                        read_chats()

                        chatReference = databaseReference.child("CHATS_V2").child(senderName!!).child(receiverName!!)
                        typingStatusReference = firebaseDatabase.getReference("typing_status/$senderId")


                        gender = session.getData(Constant.GENDER)
                        verified = session.getData(Constant.VERIFIED)


                        if (friend_verified == "1") {
                            binding.tvAbout.visibility = View.VISIBLE
                            binding.ivVerified.visibility = View.VISIBLE
                        } else {
                            binding.tvAbout.visibility = View.VISIBLE
                            binding.ivVerified.visibility = View.GONE
                        }

                        Glide.with(this)
                            .load(jsonobj.getString(Constant.PROFILE))
                            .placeholder(R.drawable.profile_placeholder)
                            .into(binding.ivProfile)


                        onResume()


                        // Toast.makeText(activity, friend, Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.OTHER_USER_DETAILS, params, true, 1)
    }


}

const val CHATS_ACTIVITY = "ChatsActivity"