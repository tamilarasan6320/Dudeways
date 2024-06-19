package com.app.dudeways.Model

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class ChatModel(
    val attachmentType: String? = null,
    val chatID: String? = null,
    val dateTime: String? = Timestamp.now().toDate().time.toString(),
    val message: String? = null,
    val msgSeen: Boolean? = false,
    val receiverID: String? = null,
    val senderID: String? = null,
    val type: String? = null,
    val sentBy: String? = null,
    val isTyping: Boolean = false
)
