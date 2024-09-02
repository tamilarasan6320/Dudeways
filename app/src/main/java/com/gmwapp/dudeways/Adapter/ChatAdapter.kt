package com.gmwapp.dudeways.Adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.Model.ChatModel
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ReceiverChatMessageBinding
import com.gmwapp.dudeways.databinding.SenderChatMessageBinding
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.zoho.livechat.android.utils.SalesIQCache.messages
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val conversations: MutableList<ChatModel?>,
    private val onClick: (ChatModel) -> Unit,
    private var session: Session,
    private val onMessageDelete: (ChatModel) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ItemHolder>() {

    inner class ItemHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatMessage: ChatModel?, position: Int) {
            chatMessage?.let {
                val messageTime = it.dateTime?.toLong() ?: 0L
                val formattedTime = formatTime(messageTime)
                val formattedDate = formatDate(messageTime)

                val shouldShowDateHeader = shouldDisplayDateHeader(position, formattedDate)

                when (binding) {
                    is SenderChatMessageBinding -> {
                        binding.tvMessage.text = it.message
                        binding.tvTime.text = formattedTime
                        handleDateHeader(binding.tvDateHeader, shouldShowDateHeader, formattedDate)
                        Glide.with(binding.root.context).load(session.getData(Constant.PROFILE))
                            .placeholder(R.drawable.profile_placeholder).into(binding.ivUserProfile)
                        handleSeenStatus(binding.tvSeenStatus, position)

                        // Long press to show delete dialog
                        binding.root.setOnLongClickListener {
                            showDeleteConfirmationDialog(chatMessage)
                            true
                        }
                    }

                    is ReceiverChatMessageBinding -> {
                        binding.tvMessage.text = it.message
                        binding.tvTime.text = formattedTime
                        handleDateHeader(binding.tvDateHeader, shouldShowDateHeader, formattedDate)
                        Glide.with(binding.root.context).load(session.getData("reciver_profile"))
                            .placeholder(R.drawable.profile_placeholder).into(binding.ivUserProfile)

                        // Long press to show delete dialog
//                        binding.root.setOnLongClickListener {
//                            showDeleteConfirmationDialog(chatMessage)
//                            true
//                        }
                    }

                    else -> {}
                }
            }
        }

        private fun showDeleteConfirmationDialog(chatMessage: ChatModel?) {
            val context = binding.root.context
            val dialogBuilder = AlertDialog.Builder(context)
                .setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Yes") { _, _ ->
                    chatMessage?.let {
                        deleteMessage(it)
                    }
                }
                .setNegativeButton("No", null)

            val dialog = dialogBuilder.create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(context, R.color.primary))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(context, R.color.text_grey))
        }

        private fun deleteMessage(chatMessage: ChatModel) {
            // Perform deletion logic here
            conversations.remove(chatMessage)
            notifyDataSetChanged()
            onMessageDelete(chatMessage)
        }

        private fun handleDateHeader(dateHeader: TextView, shouldShow: Boolean, date: String) {
            if (shouldShow) {
                dateHeader.text = date
                dateHeader.visibility = View.VISIBLE
            } else {
                dateHeader.visibility = View.GONE
            }
        }

        private fun handleSeenStatus(tvSeenStatus: TextView, position: Int) {
            if (position == conversations.size - 1) {
                val msgSeen = session.getData(Constant.MSG_SEEN).toString()
                if (msgSeen == "0") {
                    tvSeenStatus.visibility = View.VISIBLE
                    tvSeenStatus.text = ""
                } else {
                    tvSeenStatus.visibility = View.GONE
                }
            } else {
                tvSeenStatus.visibility = View.GONE
            }
        }

        private fun shouldDisplayDateHeader(position: Int, currentDate: String): Boolean {
            if (position == 0) return true
            val previousMessage = conversations[position - 1]
            val previousDate = previousMessage?.dateTime?.toLong()?.let { formatDate(it) }
            return currentDate != previousDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding: ViewBinding = when (viewType) {
            0 -> ReceiverChatMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            1 -> SenderChatMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val chatMessage = conversations[position]
        holder.bind(chatMessage, position)
    }

    override fun getItemCount(): Int = conversations.size

    fun getItemInfo(position: Int): ChatModel? = conversations[position]

    override fun getItemViewType(position: Int): Int {
        val chatMessage = conversations[position]
        val currentUser = session.getData(Constant.NAME)
        return if (chatMessage?.sentBy == currentUser) 1 else 0
    }

    private fun formatTime(timestamp: Long): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(timestamp)
    }

    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
    }

    private fun shouldDisplayDateHeader(position: Int, currentDate: String): Boolean {
        if (position == 0) return true
        val previousMessage = conversations[position - 1]
        val previousDate = previousMessage?.dateTime?.toLong()?.let { formatDate(it) }
        return currentDate != previousDate
    }

    fun addMessage(message: ChatModel) {
        conversations.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
