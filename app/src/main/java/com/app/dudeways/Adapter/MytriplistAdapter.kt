package com.app.dudeways.Adapter

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.dudeways.Activity.ProfileinfoActivity
import com.app.dudeways.Model.Chatlist
import com.app.dudeways.Model.Mytriplist
import com.app.dudeways.R
import com.bumptech.glide.Glide

class MytriplistAdapter(
    val activity: Activity,
    mytriplist: java.util.ArrayList<Mytriplist>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val mytriplist: ArrayList<Mytriplist>
    val activitys: Activity

    init {
        this.mytriplist = mytriplist
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.layout_mytriplist, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: Mytriplist = mytriplist[position]




        holder.tvName.text = report.name
        holder.tvLocation.text = report.location
        holder.tvDescription.text = report.trip_description
        holder.tvUsername.text = "@"+report.unique_name
        holder.tvDate.text = "From "+report.from_date+" to "+report.to_date
        holder.tvTitle.text = report.trip_title


        // check report.user_name is more than 10 latters
        if (report.name?.length!! > 10) {
            if (report.unique_name?.length!! > 7) {
                holder.tvUsername.text = "@"+ report.unique_name!!.substring(0, 7) + ".."
            } else {
                holder.tvUsername.text = "@"+report.unique_name
            }
        } else {
            holder.tvUsername.text = "@"+report.unique_name
        }


        if (report.trip_status.equals("0")) {
            holder.tvStatus.text = "In Review"
            holder.rlStatus.setBackgroundColor(activity.resources.getColor(R.color.blue))

        }
        if (report.trip_status.equals("1")) {
            holder.tvStatus.text = "Approved"
            holder.rlStatus.setBackgroundColor(activity.resources.getColor(R.color.green))

        }
        if (report.trip_status.equals("2")) {
            holder.tvStatus.text = "Rejected"
            holder.rlStatus.setBackgroundColor(activity.resources.getColor(R.color.red))

        }


        holder.tvmore.setOnClickListener {
            if (holder.tvDescription.visibility == View.VISIBLE) {
                holder.tvDescription.visibility = View.GONE
                holder.tvmore.text = activity.getString(R.string.more)
            } else {
                holder.tvDescription.visibility = View.VISIBLE
                holder.tvmore.text = activity.getString(R.string.less)
            }
        }







        Glide.with(activitys).load(report.trip_image).placeholder(R.drawable.placeholder_bg)
            .into(holder.ivProfileImage)

        Glide.with(activitys).load(report.profile).placeholder(R.drawable.placeholder_bg)
            .into(holder.ivProfile)


    }







    override fun getItemCount(): Int {
        return mytriplist.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvmore: TextView = itemView.findViewById(R.id.tvmore)
        val ivProfile:ImageView = itemView.findViewById(R.id.ivProfile)
        val rlStatus:RelativeLayout = itemView.findViewById(R.id.rlStatus)
        val tvStatus:TextView = itemView.findViewById(R.id.tvStatus)





    }


}