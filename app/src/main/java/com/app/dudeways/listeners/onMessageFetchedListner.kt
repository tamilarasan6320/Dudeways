package com.app.dudeways.listeners

import com.app.dudeways.Model.ChatModel



interface OnMessagesFetchedListener {

    /**
     * Called when messages are fetched from the API.
     * Used to fetch the initial conversation.
     */
    fun onMessagesFetched(conversations: MutableList<ChatModel?>)

    /**
     * Called when a new message is added.
     * Used to update the conversation.
     */
    fun onMessageAdded(chatModel: ChatModel?)

    /**
     * Called when a message is changed.
     * Used to update the conversation.
     */
    fun onMessageChanged(chatModel: ChatModel?)

    /**
     * Called when a message is removed.
     * Used to update the conversation.
     */
    fun onMessageRemoved(chatModel: ChatModel?)

    /**
     * Called when an error occurs.
     * Used to handle errors.
     */
    fun onError(errorMessage: String)
}