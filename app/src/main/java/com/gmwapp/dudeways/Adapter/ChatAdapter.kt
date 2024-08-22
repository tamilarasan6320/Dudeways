package com.gmwapp.dudeways.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.gmwapp.dudeways.Model.ChatModel
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ReceiverChatMessageBinding
import com.gmwapp.dudeways.databinding.SenderChatMessageBinding
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val conversations: List<ChatModel?>,
    private val onClick: (ChatModel) -> Unit,
    private var session: Session
) : RecyclerView.Adapter<ChatAdapter.ItemHolder>() {

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
                        if (shouldShowDateHeader) {
                            binding.tvDateHeader.text = formattedDate
                            binding.tvDateHeader.visibility = View.VISIBLE
                        } else {
                            binding.tvDateHeader.visibility = View.GONE
                        }
                        Glide.with(binding.root.context).load(session.getData(Constant.PROFILE))
                            .placeholder(R.drawable.profile_placeholder).into(binding.ivUserProfile)

                        // Set seen status visibility
                        if (position == conversations.size - 1) {
                            // Last message
                            binding.tvSeenStatus.visibility = View.VISIBLE
                            binding.tvSeenStatus.text = if (it.msgSeen == true) "Seen" else "Delivered"
                        } else {
                            binding.tvSeenStatus.visibility = View.GONE
                        }
                    }

                    is ReceiverChatMessageBinding -> {
                        binding.tvMessage.text = it.message
                        binding.tvTime.text = formattedTime
                        if (shouldShowDateHeader) {
                            binding.tvDateHeader.text = formattedDate
                            binding.tvDateHeader.visibility = View.VISIBLE
                        } else {
                            binding.tvDateHeader.visibility = View.GONE
                        }
                        Glide.with(binding.root.context).load(session.getData("reciver_profile"))
                            .placeholder(R.drawable.profile_placeholder).into(binding.ivUserProfile)
                    }

                    else -> {}
                }
            }
        }

        private fun shouldDisplayDateHeader(position: Int, currentDate: String): Boolean {
            if (position == 0) return true // Show date at the start of the chat
            val previousMessage = conversations[position - 1]
            val previousDate = previousMessage?.dateTime?.toLong()?.let { formatDate(it) }
            return currentDate != previousDate
        }
    }


    private fun formatTime(timestamp: Long): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(timestamp)
    }

    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        val currentCalendar = Calendar.getInstance()

        // Setting the time to the message timestamp
        calendar.timeInMillis = timestamp

        // Getting the formatted date
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        return when {
            isToday(calendar, currentCalendar) -> "Today"
            isYesterday(calendar, currentCalendar) -> "Yesterday"
            else -> dateFormat.format(calendar.time)
        }
    }

    private fun isToday(calendar: Calendar, currentCalendar: Calendar): Boolean {
        return calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(calendar: Calendar, currentCalendar: Calendar): Boolean {
        currentCalendar.add(Calendar.DAY_OF_YEAR, -1)
        return calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)
    }
}
