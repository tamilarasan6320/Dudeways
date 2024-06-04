package com.app.dudeways.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.app.dudeways.Model.ChatModel
import com.app.dudeways.databinding.ReceiverChatMessageBinding
import com.app.dudeways.databinding.SenderChatMessageBinding
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.bumptech.glide.Glide

class ChatAdapter(
    private val conversations: List<ChatModel?>,
    private val onClick: (ChatModel) -> Unit,
    private var session : Session,
) : RecyclerView.Adapter<ChatAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding: ViewBinding? = when (viewType) {
            0 -> SenderChatMessageBinding.inflate(LayoutInflater.from(parent.context))
            1 -> ReceiverChatMessageBinding.inflate(LayoutInflater.from(parent.context))
            else -> null
        }
        return ItemHolder(
            binding!!
        )
    }

    override fun onBindViewHolder(holderParent: ItemHolder, position: Int) {
        val recentChats = conversations[position]
        holderParent.bind(recentChats)
    }


    override fun getItemCount(): Int {
        return conversations.size
    }


    override fun getItemViewType(position: Int): Int {
        val conversations = conversations[position]
        val currentUser = session.getData(Constant.NAME)
        return when (conversations?.sentBy) {
            currentUser -> 1
            else -> 0
        }
    }

    inner class ItemHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatModel?) {
            when (binding) {
                is SenderChatMessageBinding -> {
                    binding.TVMessage.text = chatMessage?.message
                    Glide
                        .with(binding.root.context)
                        .load(session.getData("reciver_profile"))
                        .into(binding.IVUserProfile)
                }

                is ReceiverChatMessageBinding -> {
                    binding.TVMessage.text = chatMessage?.message
                    Glide
                        .with(binding.root.context)
                        .load(session.getData(Constant.PROFILE))
                        .into(binding.IVUserProfile)
                }
            }
        }
    }

}