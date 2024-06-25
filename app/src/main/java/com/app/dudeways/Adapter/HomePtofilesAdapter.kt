package com.app.dudeways.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.dudeways.Activity.ChatsActivity
import com.app.dudeways.Activity.FreePointsActivity
import com.app.dudeways.Activity.ProfileinfoActivity
import com.app.dudeways.Activity.PurchasepointActivity
import com.bumptech.glide.Glide
import com.app.dudeways.Model.HomeProfile
import com.app.dudeways.R
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject

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

        val session = Session(activity)

        holder.tvName.text = report.name
        holder.tvLocation.text = report.location
        holder.tvDescription.text = report.trip_description
        holder.tvUsername.text = "@"+report.unique_name
        holder.tvDate.text = "From "+report.from_date+" to "+report.to_date
        holder.tvTitle.text = report.trip_title
        holder.tvKm.text = ""+report.distance

        // \u00B7 + time
        holder.tvtime.text = "\u00B7 "+report.time


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

        //holder.tvUsername only show 5 latters end with ..

        holder.tvmore.setOnClickListener {
            if (holder.tvDescription.visibility == View.VISIBLE) {
                holder.tvDescription.visibility = View.GONE
                holder.tvmore.text = activity.getString(R.string.more)
            } else {
                holder.tvDescription.visibility = View.VISIBLE
                holder.tvmore.text = activity.getString(R.string.less)
            }
        }


        var friend_data: String

        if (report.verified == "1") {
            holder.ivVerify.visibility = View.VISIBLE
        } else {
            holder.ivVerify.visibility = View.GONE
        }


        friend_data = "" + report.friend
        if (friend_data == "0") {
            holder.ivaddFriend.setBackgroundResource(R.drawable.add_account)
            holder.tvAddFriend.text = "Add to Friend"
        } else if (friend_data == "1") {
            holder.ivaddFriend.setBackgroundResource(R.drawable.added_frd)
            holder.tvAddFriend.text = "Friend Added"
        }


        holder.rlAddFriend.setOnClickListener {
            // Change the background of rlAddFriend
            if (friend_data == "0") {
                val friend = "1"
                friend_data = "1"
                add_freind(holder.ivaddFriend, holder.tvAddFriend, report.user_id,friend)
            } else if (friend_data == "1"   ) {
                val friend = "2"
                 friend_data = "0"
                add_freind(holder.ivaddFriend, holder.tvAddFriend, report.user_id,friend)
            }




        }

        holder.llProfile.setOnClickListener {
            val intent = Intent(activity, ProfileinfoActivity::class.java)
            intent.putExtra("name", report.name)
            intent.putExtra("chat_user_id", report.user_id)
            intent.putExtra("id", report.id)
            session.setData("reciver_profile", report.profile)
            intent.putExtra("friend", report.friend)
            activity.startActivity(intent)

        }


        val point = session.getData(Constant.POINTS)



        holder.rlChat.setOnClickListener {


            // point is less the 10
            if (report.user_id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", report.id)
                intent.putExtra("name", report.name)
                session.setData("reciver_profile", report.profile)
                intent.putExtra("chat_user_id", report.user_id)
                activity.startActivity(intent)
            }


        }

        Glide.with(activitys).load(report.trip_image).placeholder(R.drawable.placeholder_bg)
            .into(holder.ivProfileImage)

        Glide.with(activitys).load(report.profile).placeholder(R.drawable.profile_placeholder)
            .into(holder.ivProfile)


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
        val ivVerify: ImageView = itemView.findViewById(R.id.ivVerify)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvmore: TextView = itemView.findViewById(R.id.tvmore)
        val ivProfile:ImageView = itemView.findViewById(R.id.ivProfile)
        val rlChat:RelativeLayout = itemView.findViewById(R.id.rlChat)
        val llProfile:LinearLayout = itemView.findViewById(R.id.llProfile)
        val tvKm:TextView = itemView.findViewById(R.id.tvKm)
        val tvtime:TextView = itemView.findViewById(R.id.tvtime)



    }


    private fun add_freind(
        ivaddFriend: ImageView,
        tvAddFriend: TextView,
        user_id: String?,
        friend: String
    ) {
        val session = Session(activity)
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.FRIEND_USER_ID] = user_id!!
        params[Constant.FRIEND] = friend

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {


                        if (friend == "1") {
                            ivaddFriend.setBackgroundResource(R.drawable.added_frd)
                            tvAddFriend.text = "Friend Added"

                        } else if (friend == "2") {
                            ivaddFriend.setBackgroundResource(R.drawable.add_account)
                            tvAddFriend.text = "Add to Friend"
                        }

                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()


                    } else {
                        Toast.makeText(
                            activity,
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            // Stop the refreshing animation once the network request is complete

        }, activity, Constant.ADD_FRIENDS, params, true, 1)
    }



}