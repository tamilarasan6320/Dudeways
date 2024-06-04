package com.app.dudeways.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.dudeways.Adapter.ChatAdapter
import com.app.dudeways.Model.ChatModel
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityChatsBinding
import com.app.dudeways.databinding.ActivityHomeBinding
import com.app.dudeways.extentions.makeToast
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
import kotlin.random.Random

class ChatsActivity : AppCompatActivity() , OnMessagesFetchedListener {

    lateinit var binding: ActivityChatsBinding
    lateinit var activity: Activity
    lateinit var session: Session
    private var isLoading: Boolean = true
    private val firebaseDatabase: FirebaseDatabase = Firebase.database("https://dudeways-c8f31-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val databaseReference: DatabaseReference = firebaseDatabase.reference
    private var chatAdapter: ChatAdapter? = null
    private var messages = mutableListOf<ChatModel?>()

    var sender_id = ""
    var receiver_id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)
        binding = ActivityChatsBinding.inflate(layoutInflater)
        activity = this
        session = Session(activity)
        setContentView(binding.root)


        sender_id = session.getData(Constant.USER_ID)
        receiver_id = intent.getStringExtra("user_id").toString()

        binding.tvName.text = intent.getStringExtra("name")
        Glide.with(activity)
            .load(session.getData("reciver_profile"))
            .placeholder(R.drawable.placeholder_bg)
            .into(binding.ivProfile)


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.sendButton?.setOnClickListener {
            binding?.messageEdittext?.text?.let { message ->
                if (message.isNotEmpty()) {
                    updateMessages(
                        sender_id,
                        receiver_id,
                        message = message.toString()
                    )
                    message.clear()
                } else {
                    makeToast("Enter text to send")
                }
            }
        }

        fetchMessages(
            senderID = sender_id,
            receiverID = receiver_id,
            onMessagesFetchedListener = this
        )
        binding?.progressCircular?.visibility = if (isLoading) View.VISIBLE else View.GONE


    }


    private fun fetchMessages(
        senderID: String,
        receiverID: String,
        onMessagesFetchedListener: OnMessagesFetchedListener
    ) {
        val conversations: MutableList<ChatModel?> = mutableListOf()
        val reference = databaseReference
            .child("CHATS_V2")
            .child(senderID)
            .child(receiverID)


        reference
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    for (child in dataSnapshot.children) {
                        val chatModel = child.getValue(ChatModel::class.java)
                        if (chatModel != null) {
                            Log
                                .e("Conversations", chatModel.toString())
                            conversations
                                .add(chatModel)
                        }
                    }
                }
                onMessagesFetchedListener.onMessagesFetched(conversations)
            }
            .addOnFailureListener { exception ->
                onMessagesFetchedListener.onError(exception.message.toString())
            }

        reference.addChildEventListener(
            object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatModel = snapshot.getValue(ChatModel::class.java)
                    onMessagesFetchedListener
                        .onMessageAdded(chatModel)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatModel = snapshot.getValue(ChatModel::class.java)
                    onMessagesFetchedListener
                        .onMessageChanged(chatModel)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val chatModel = snapshot.getValue(ChatModel::class.java)
                    onMessagesFetchedListener
                        .onMessageRemoved(chatModel)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    //
                }

                override fun onCancelled(error: DatabaseError) {
                    //
                }
            }
        )
    }

    private fun updateMessages(
        senderID: String,
        receiverID: String,
        message: String,
    ) {
        val chatID: String = Random.nextInt(100000, 999999).toString()
        databaseReference
            .child("CHATS_V2")
            .child(senderID)
            .child(receiverID)
            .child(chatID)
            .setValue(
                ChatModel(
                    attachmentType = "TEXT",
                    chatID = chatID,
                    dateTime = Timestamp.now().toDate().time.toString(),
                    message = message,
                    msgSeen = false,
                    receiverID = receiverID,
                    senderID = senderID,
                    type = "TEXT",
                    sentBy = session?.getData(Constant.NAME)
                )
            )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    makeToast("Messages updated")
                }
            }
    }

    override fun onMessagesFetched(conversations: MutableList<ChatModel?>) {
        messages = conversations
        with(messages) {
            if (isEmpty()) {
                //Empty Conversation
                isLoading = false
                makeToast("Empty message")
            } else {
                messages.sortBy {
                    it?.dateTime
                }
                chatAdapter = ChatAdapter(
                    messages,
                    onClick = {

                    },
                    session!!
                )
                binding.RVChats?.apply {
                    layoutManager = LinearLayoutManager(this@ChatsActivity)
                    adapter = chatAdapter
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageAdded(chatModel: ChatModel?) {
        chatModel?.let {
            if (messages.none { existingChatModel -> existingChatModel?.chatID == chatModel.chatID }) {
                messages.add(chatModel)
                messages.sortBy {
                    it?.dateTime
                }
                chatAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onMessageChanged(chatModel: ChatModel?) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageRemoved(chatModel: ChatModel?) {
        messages
            .remove(chatModel)
        chatAdapter?.notifyDataSetChanged()
    }

    override fun onError(errorMessage: String) {

    }
}