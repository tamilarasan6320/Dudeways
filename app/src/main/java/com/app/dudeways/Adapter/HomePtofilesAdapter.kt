package com.app.dudeways.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.dudeways.Model.HomeProfile
import com.app.dudeways.R

class HomePtofilesAdapter(
    val activity: Activity,
    homeProfile: java.util.ArrayList<HomeProfile>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val homeProfile: ArrayList<HomeProfile>
    val activitys: Activity

    init {
        this.homeProfile = homeProfile
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.layout_home_profiles, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: HomeProfile = homeProfile[position]


        holder.tvName.text = report.user_name
        holder.tvLocation.text = report.to_location
        holder.tvDescription.text = report.trip_description
        holder.tvUsername.text = "@"+report.unique_name
        holder.tvDate.text = "From "+report.from_date+" to "+report.to_date



        holder.rlAddFriend.setOnClickListener {
            // Change the background of rlAddFriend
            holder.ivaddFriend.setBackgroundResource(R.drawable.added_frd)
            holder.tvAddFriend.text = "Friend Added"


        }

        Glide.with(activitys).load(report.user_profile).placeholder(R.drawable.placeholder_bg)
            .into(holder.ivProfileImage)


    }


    override fun getItemCount(): Int {
        return homeProfile.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        val ivaddFriend: ImageView = itemView.findViewById(R.id.ivaddFriend)
        val rlAddFriend: RelativeLayout = itemView.findViewById(R.id.rlAddFriend)
        val tvAddFriend: TextView = itemView.findViewById(R.id.tvAddFriend)


    }


}