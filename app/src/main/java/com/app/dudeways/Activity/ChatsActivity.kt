package com.app.dudeways.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        senderId = session.getData(Constant.USER_ID)
        receiverId = intent.getStringExtra("chat_user_id").toString()
        /**
         * Name of the sender (Current User)
         */
        senderName = session.getData(Constant.NAME)
        /**
         * Name of the receiver (Opposite User)
         */
        receiverName = intent.getStringExtra("name")
        binding.tvName.text = receiverName

        Glide.with(this)
            .load(session.getData("reciver_profile"))
            .placeholder(R.drawable.placeholder_bg)
            .into(binding.ivProfile)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher
                .onBackPressed()
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        sentTone = soundPool.load(this, R.raw.sent_tone, 1)
        receiveTone = soundPool.load(this, R.raw.receive_tone, 1)

        binding.sendButton.setOnClickListener {
            val message = binding.messageEdittext.text.toString()
            message.takeIf { it.isNotEmpty() }?.let { msg ->
                senderName?.let { sName ->
                    receiverName?.let { rName ->
                        updateMessagesForSender(senderId, receiverId, sName, rName, msg)
                    } ?: logError(CHATS_ACTIVITY, "Unable to send your message.")
                } ?: logError(CHATS_ACTIVITY, "Unable to send your message.")
                binding.messageEdittext.text.clear()
            } ?: makeToast("Enter text to send")
        }

        senderName?.let { sName ->
            receiverName?.let { rName ->
                fetchMessages(sName, rName, this)
            } ?: logError(CHATS_ACTIVITY, "Unable to send your message.")
        } ?: logError(CHATS_ACTIVITY, "Unable to send your message.")

    }

    private fun fetchMessages(
        senderName: String,
        receiverName: String,
        onMessagesFetchedListener: OnMessagesFetchedListener
    ) {
        val conversations: MutableList<ChatModel?> = mutableListOf()
        val reference = databaseReference.child("CHATS_V2").child(senderName).child(receiverName)

        reference.get().addOnSuccessListener { dataSnapshot ->
            dataSnapshot.takeIf { it.exists() }?.let { conversationSnapShot ->
                for (child in conversationSnapShot.children) {
                    val chatModel = child.getValue(ChatModel::class.java)
                    chatModel.takeIf { it != null }?.let { conversationModel ->
                        logInfo(CHATS_ACTIVITY, "ChatModel: $chatModel")
                        conversations.add(conversationModel)
                    } ?: logError(CHATS_ACTIVITY, "Unable to load your conversations.")
                }
            } ?: logInfo(CHATS_ACTIVITY, "No messages found.")
            onMessagesFetchedListener.onMessagesFetched(conversations)
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
                Log.e(CHATS_ACTIVITY, "onChildChanged - ChatModel: $chatModel")
                onMessagesFetchedListener.onMessageChanged(chatModel)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val chatModel = snapshot.getValue(ChatModel::class.java)
                Log.e(CHATS_ACTIVITY, "onChildRemoved - ChatModel: $chatModel")
                onMessagesFetchedListener.onMessageRemoved(chatModel)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // No implementation needed
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(CHATS_ACTIVITY, "onCancelled - DatabaseError: ${error.message}")
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
                task.takeIf { it.isSuccessful }?.let {
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
                } ?: logError(CHATS_ACTIVITY, "Failed to send message")
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

    private fun playSentTone() {
        soundPool.setOnLoadCompleteListener { _, _, _ -> }
        soundPool.play(sentTone, 1f, 1f, 0, 0, 1f)
    }

    private fun playReceiveTone() {
        soundPool.setOnLoadCompleteListener { _, _, _ -> }
        soundPool.play(receiveTone, 1f, 1f, 0, 0, 1f)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onMessagesFetched(conversations: MutableList<ChatModel?>) {
        messages = conversations
        with(messages) {
            takeIf { isNotEmpty() }?.let {
                messages.sortBy { it?.dateTime }
                chatAdapter = ChatAdapter(messages, onClick = {}, session)
                chatAdapter?.notifyDataSetChanged()
                binding.RVChats.apply {
                    layoutManager = LinearLayoutManager(this@ChatsActivity)
                    adapter = chatAdapter
                }
            } ?: logError("conversations", "Conversations are empty.")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageAdded(chatModel: ChatModel?) {
        chatModel?.let {
            if (messages.none { existingChatModel -> existingChatModel?.chatID == chatModel.chatID }) {
                messages.add(chatModel)
                playReceiveTone()
                messages.sortBy { it?.dateTime }
                chatAdapter?.notifyDataSetChanged()
                Log.e("onMessageAdded", "Message added: $chatModel")
            }
        }
    }

    override fun onMessageChanged(chatModel: ChatModel?) {
        Log.e("onMessageChanged", "Message changed: $chatModel")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageRemoved(chatModel: ChatModel?) {
        messages.remove(chatModel)
        chatAdapter?.notifyDataSetChanged()
        Log.e("onMessageRemoved", "Message removed: $chatModel")
    }

    override fun onError(errorMessage: String) {
        makeToast("Error: $errorMessage")
        Log.e("onError", "Error: $errorMessage")
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
                        logInfo(
                            CHATS_ACTIVITY,
                            "Message update to the API."
                        )
                    } else {
                        logError(
                            CHATS_ACTIVITY,
                            "Message failed to update to the API."
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.ADD_CHAT, params, true, 1)
    }
}

private const val CHATS_ACTIVITY = "ChatsActivity"

