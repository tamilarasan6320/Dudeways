package com.app.dudeways.Adapter

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.dudeways.Activity.ChatsActivity
import com.app.dudeways.Activity.ProfileinfoActivity
import com.app.dudeways.Model.Chatlist
import com.app.dudeways.R
import com.bumptech.glide.Glide

class ChatlistAdapter(
    val activity: Activity,
    chatlist: java.util.ArrayList<Chatlist>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val chatlist: ArrayList<Chatlist>
    val activitys: Activity

    init {
        this.chatlist = chatlist
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.layout_chatlist, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: Chatlist = chatlist[position]


        holder.TV_user_name.text = report.name
        holder.TV_message_content.text = report.latest_message

        if (report.msg_seen == "1") {
            holder.IC_read.visibility = View.VISIBLE
        } else {
            holder.IC_read.visibility = View.GONE
        }

        holder.TV_sent_time.text = report.latest_msg_time

        Glide.with(activitys)
            .load(report.profile)
            .placeholder(R.drawable.placeholder_bg)
            .into(holder.IV_user_profile)


        holder.itemView.setOnClickListener{
            val intent = Intent(activity, ChatsActivity::class.java)
            activity.startActivity(intent)
        }






    }


    override fun getItemCount(): Int {
        return chatlist.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
        val TV_user_name: TextView = itemView.findViewById(R.id.TV_user_name)
        val IC_read: ImageView = itemView.findViewById(R.id.IC_read)
        val IV_user_profile: ImageView = itemView.findViewById(R.id.IV_user_profile)
        val TV_message_content: TextView = itemView.findViewById(R.id.TV_message_content)
        val TV_sent_time: TextView = itemView.findViewById(R.id.TV_sent_time)


    }


}