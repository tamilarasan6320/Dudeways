package com.app.dudeways.extentions

import android.annotation.SuppressLint
import android.media.SoundPool
import android.text.format.DateUtils
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.app.dudeways.Activity.CHATS_ACTIVITY
import com.app.dudeways.Activity.ChatsActivity
import com.app.dudeways.Adapter.ChatAdapter
import com.app.dudeways.Model.ChatModel
import com.app.dudeways.R
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.listeners.OnMessagesFetchedListener
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONException
import org.json.JSONObject
import kotlin.random.Random


fun ChatsActivity.popUpMenu(
    senderID: String,
    receiverID: String,
    firebaseDatabase: FirebaseDatabase,
) {
    val popupMenu = PopupMenu(this, binding.ivMore)
    popupMenu.inflate(R.menu.chat_options_menu)
    isBlocked(
        senderID,
        receiverID,
        firebaseDatabase
    ) { blocked ->
        val blockMenuItem = popupMenu.menu.findItem(R.id.menu_block_chat)
        blockMenuItem.title = if (blocked) "Unblock User" else "Block User"

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_block_chat -> {
                    if (blocked) {
                        // Unblock user
                        updateBlockStatus(senderID, receiverID, firebaseDatabase, false)
                    } else {
                        // Block user
                        updateBlockStatus(senderID, receiverID, firebaseDatabase, true)
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

private fun ChatsActivity.isBlocked(
    senderID: String,
    receiverId: String,
    firebaseDatabase: FirebaseDatabase,
    onBlock: (Boolean) -> Unit
) {
    val blockStatusRef = firebaseDatabase.getReference("block_status")

    blockStatusRef.child(senderID).child(receiverId).get()
        .addOnSuccessListener { snapshot ->
            val isBlocked = snapshot.getValue(Boolean::class.java) ?: false
            onBlock(isBlocked)
        }
        .addOnFailureListener { exception ->
            logError(CHATS_ACTIVITY, "Error checking block status: ${exception.message}")
            onBlock(false) // Default to not blocked in case of error
        }
}

private fun ChatsActivity.updateBlockStatus(
    senderId: String,
    receiverId: String,
    firebaseDatabase: FirebaseDatabase,
    blocked: Boolean
) {
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


fun ChatsActivity.report() {
    // Implement report functionality
}


@SuppressLint("NotifyDataSetChanged")
fun ChatsActivity.clearLocalMessages(
    messages: MutableList<ChatModel?>,
    chatAdapter: ChatAdapter
) {
    messages.clear()
    chatAdapter.notifyDataSetChanged()
}

fun ChatsActivity.clearChatInFirebase(
    senderName: String,
    receiverName: String,
    databaseReference: DatabaseReference
) {
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
fun ChatsActivity.setUserStatus(
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

/**
 *  Observes typing status and updates the UI accordingly.
 */
fun ChatsActivity.observeTypingStatus(
    firebaseDatabase: FirebaseDatabase,
    receiverID: String,
) {
    firebaseDatabase.getReference("typing_status/$receiverID")
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
fun ChatsActivity.observeUserStatus(
    firebaseDatabase: FirebaseDatabase,
    receiverId: String
) {
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
fun ChatsActivity.fetchMessages(
    chatReference: DatabaseReference?,
    onMessagesFetchedListener: OnMessagesFetchedListener,
    isConversationsFetching: (Boolean) -> Unit
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
            isConversationsFetching(false)
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
fun ChatsActivity.updateMessagesForSender(
    databaseReference: DatabaseReference,
    senderID: String,
    receiverID: String,
    senderName: String,
    receiverName: String,
    message: String,
    soundPool: SoundPool,
    sentTone: Int
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
                addChat(message,receiverID)
                updateMessagesForReceiver(
                    senderID,
                    receiverID,
                    chatID,
                    senderName,
                    receiverName,
                    message,
                    databaseReference
                )
                playSentTone(soundPool, sentTone)
                logInfo(CHATS_ACTIVITY, "Message sent")
            } else {
                logError(CHATS_ACTIVITY, "Failed to send message")
            }
        }
}

/**
 *  Update the message for receiver in firebase.
 */
private fun ChatsActivity.updateMessagesForReceiver(
    senderID: String,
    receiverID: String,
    chatID: String,
    senderName: String,
    receiverName: String,
    message: String,
    databaseReference: DatabaseReference
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
private fun ChatsActivity.playSentTone(
    soundPool: SoundPool,
    sentTone: Int
) {
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
fun ChatsActivity.playReceiveTone(
    soundPool: SoundPool,
    receiveTone: Int
) {
    logInfo(CHATS_ACTIVITY, "Attempting to play receive tone")
    val result = soundPool.play(receiveTone, 1f, 1f, 0, 0, 1f)
    if (result == 0) {
        logError(CHATS_ACTIVITY, "Failed to play receive tone")
    } else {
        logInfo(CHATS_ACTIVITY, "Receive tone played successfully")
    }
}


fun ChatsActivity.addChat(
    message: String,
    receiverID: String
) {
    val params: MutableMap<String, String> = HashMap()
    params[Constant.USER_ID] = session.getData(Constant.USER_ID)
    params[Constant.CHAT_USER_ID] = receiverID
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

private fun ChatsActivity.updateMessageSeenStatus(
    databaseReference: DatabaseReference,
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

