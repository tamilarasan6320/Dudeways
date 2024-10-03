package com.gmwapp.dudeways.Adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Activity.ProfileinfoActivity
import com.gmwapp.dudeways.Model.Notification
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.helper.Session
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.Activity.ChatsActivity
import com.gmwapp.dudeways.Model.Wallet
import com.gmwapp.dudeways.helper.Constant

class WalletAdapter(
    val activity: Activity,
    wallet: java.util.ArrayList<Wallet>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val wallet: ArrayList<Wallet>
    val activitys: Activity

    init {
        this.wallet = wallet
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.layout_wallet, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: Wallet = wallet[position]
        val session = Session(activity)


       holder.tvName.text = report.name
       holder.tvMessage.text = report.message
       holder.tvtime.text = report.time
        Glide.with(activitys).load(report.profile).placeholder(R.drawable.profile_placeholder)
            .into(holder.civProfile)

//        holder.civProfile.setOnClickListener {
//            val intent = Intent(activity, ProfileinfoActivity::class.java)
//            intent.putExtra("name", report.name)
//            intent.putExtra("chat_user_id", report.notify_user_id.toString())
//            intent.putExtra("id", report.id.toString())
//            session.setData("reciver_profile", report.profile)
//            activity.startActivity(intent)
//
//        }

        if (report.verified == "1") {
            holder.ivVerify.visibility = View.VISIBLE
        } else {
            holder.ivVerify.visibility = View.GONE
        }


        holder.itemView.setOnClickListener{

            if (report.notify_user_id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", report.id)
                intent.putExtra("name", report.name)
                session.setData("reciver_profile", report.profile)
                intent.putExtra("chat_user_id", report.notify_user_id)
                intent.putExtra("unique_name", report.unique_name)
                intent.putExtra("friend_verified", report.verified)
                activity.startActivity(intent)
            }



        }

    }


    override fun getItemCount(): Int {
        return wallet.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvtime: TextView = itemView.findViewById(R.id.tvtime)
        val civProfile: ImageView = itemView.findViewById(R.id.civProfile)
        val ivVerify: ImageView = itemView.findViewById(R.id.ivVerify)


    }


}