package com.app.dudeways.Adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.dudeways.Activity.ChatsActivity
import com.app.dudeways.Activity.ProfileinfoActivity
import com.app.dudeways.Model.Connect
import com.app.dudeways.R
import com.app.dudeways.helper.Session
import com.bumptech.glide.Glide

class ConnectAdapter(
    val activity: Activity,
    connect: java.util.ArrayList<Connect>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val connect: ArrayList<Connect>
    val activitys: Activity

    init {
        this.connect = connect
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.layout_home_connect, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: Connect = connect[position]

        val session = Session(activity)


        holder.itemView.setOnClickListener{
            val intent = Intent(activity, ProfileinfoActivity::class.java)
            activity.startActivity(intent)
        }


        holder.rlChat.setOnClickListener {
            val intent = Intent(activity, ChatsActivity::class.java)
            intent.putExtra("id", report.id)
            intent.putExtra("name", report.name)
            session.setData("reciver_profile", report.profile)
            intent.putExtra("chat_user_id", report.friend_user_id)
            activity.startActivity(intent)
        }


       holder.tvName.text = report.name
       holder.tvLatestseen.text = report.last_seen

        Glide.with(activitys)
            .load(report.profile)
            .placeholder(R.drawable.placeholder_bg)
            .into(holder.ivProfile)


    }


    override fun getItemCount(): Int {
        return connect.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
       val tvName: TextView = itemView.findViewById(R.id.tvName)
       val tvLatestseen: TextView = itemView.findViewById(R.id.tvLatestseen)
        val ivProfile:ImageView = itemView.findViewById(R.id.ivProfile)
        val rlChat:RelativeLayout = itemView.findViewById(R.id.rlChat)


    }


}