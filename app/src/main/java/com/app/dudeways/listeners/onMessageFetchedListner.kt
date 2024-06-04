package com.app.dudeways.listeners

import com.app.dudeways.Model.ChatModel



interface OnMessagesFetchedListener {
    fun onMessagesFetched(conversations: MutableList<ChatModel?>)
    fun onMessageAdded(chatModel: ChatModel?)
    fun onMessageChanged(chatModel: ChatModel?)
    fun onMessageRemoved(chatModel: ChatModel?)
    fun onError(errorMessage: String)
}