package com.app.dudeways.Activity

import android.annotation.SuppressLint
import android.app.Activity
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
    private var isLoading: Boolean = true
    private val firebaseDatabase: FirebaseDatabase = Firebase.database("https://dudeways-c8f31-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val databaseReference: DatabaseReference = firebaseDatabase.reference
    private var chatAdapter: ChatAdapter? = null
    private var messages = mutableListOf<ChatModel?>()

    var sender_id = ""
    var receiver_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        sender_id = session.getData(Constant.USER_ID)
        receiver_id = intent.getStringExtra("chat_user_id").toString()

        binding.tvName.text = intent.getStringExtra("name")
        Glide.with(activity)
            .load(session.getData("reciver_profile"))
            .placeholder(R.drawable.placeholder_bg)
            .into(binding.ivProfile)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.sendButton.setOnClickListener {
            val message = binding.messageEdittext.text.toString()
            if (message.isNotEmpty()) {
                updateMessages(sender_id, receiver_id, message)
                binding.messageEdittext.text.clear()
            } else {
                makeToast("Enter text to send")
            }
        }

        fetchMessages(sender_id, receiver_id, this)
        binding.progressCircular.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun fetchMessages(senderID: String, receiverID: String, onMessagesFetchedListener: OnMessagesFetchedListener) {
        val conversations: MutableList<ChatModel?> = mutableListOf()
        val reference = databaseReference.child("CHATS_V2").child(senderID).child(receiverID)

        reference.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                for (child in dataSnapshot.children) {
                    val chatModel = child.getValue(ChatModel::class.java)
                    if (chatModel != null) {
                        Log.e("fetchMessages", "ChatModel: $chatModel")
                        conversations.add(chatModel)
                    }
                }
            } else {
                Log.e("fetchMessages", "No messages found")
            }
            onMessagesFetchedListener.onMessagesFetched(conversations)
        }.addOnFailureListener { exception ->
            Log.e("fetchMessages", "Error fetching messages: ${exception.message}")
            onMessagesFetchedListener.onError(exception.message.toString())
        }

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatModel = snapshot.getValue(ChatModel::class.java)
                Log.e("onChildAdded", "ChatModel: $chatModel")
                onMessagesFetchedListener.onMessageAdded(chatModel)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatModel = snapshot.getValue(ChatModel::class.java)
                Log.e("onChildChanged", "ChatModel: $chatModel")
                onMessagesFetchedListener.onMessageChanged(chatModel)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val chatModel = snapshot.getValue(ChatModel::class.java)
                Log.e("onChildRemoved", "ChatModel: $chatModel")
                onMessagesFetchedListener.onMessageRemoved(chatModel)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // No implementation needed
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("onCancelled", "DatabaseError: ${error.message}")
            }
        })
    }

    private fun updateMessages(senderID: String, receiverID: String, message: String) {
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
        Log.e("updateMessages", "Sending message: $chatModel")
        databaseReference.child("CHATS_V2").child(senderID).child(receiverID).child(chatID)
            .setValue(chatModel).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addchat(message)
                    makeToast("Message sent")
                    Log.e("updateMessages", "Message sent successfully")
                } else {
                    makeToast("Failed to send message")
                    Log.e("updateMessages", "Failed to send message: ${task.exception?.message}")
                }
            }
    }

    override fun onMessagesFetched(conversations: MutableList<ChatModel?>) {
        messages = conversations
        with(messages) {
            if (isEmpty()) {
                isLoading = false
                makeToast("Empty message")
            } else {
                messages.sortBy { it?.dateTime }
                chatAdapter = ChatAdapter(messages, onClick = {}, session)
                binding.RVChats.apply {
                    layoutManager = LinearLayoutManager(this@ChatsActivity)
                    adapter = chatAdapter
                }
                isLoading = false
                binding.progressCircular.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageAdded(chatModel: ChatModel?) {
        chatModel?.let {
            if (messages.none { existingChatModel -> existingChatModel?.chatID == chatModel.chatID }) {
                messages.add(chatModel)
                messages.sortBy { it?.dateTime }
                chatAdapter?.notifyDataSetChanged()
                Log.e("onMessageAdded", "Message added: $chatModel")
            }
        }
    }

    override fun onMessageChanged(chatModel: ChatModel?) {
        Log.e("onMessageChanged", "Message changed: $chatModel")
        // Handle message update if necessary
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

    private fun addchat(message: String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.CHAT_USER_ID] = receiver_id
        params[Constant.MESSAGE] = message
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.ADD_CHAT, params, true, 1)
    }
}

