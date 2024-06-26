package com.app.dudeways.Model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import java.util.Date

@Keep
data class ChatModel(
    val attachmentType: String? = null,
    val chatID: String? = null,
    val dateTime: Long? = Timestamp.now().toDate().time,
    val message: String? = null,
    val msgSeen: Boolean? = false,
    val receiverID: String? = null,
    val senderID: String? = null,
    val type: String? = null,
    val sentBy: String? = null,
    val typing: Boolean = false
)
