package com.gmwapp.dudeways.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Activity.MytripsActivity
import com.gmwapp.dudeways.Model.Mytriplist
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.helper.Constant
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.helper.ApiConfig
import com.google.android.material.button.MaterialButton
import org.json.JSONException
import org.json.JSONObject

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

        if (report.trip_status == "0") {
            holder.tvStatus.text = "In Review"
            holder.tvStatus.setBackgroundColor(activity.resources.getColor(R.color.yellow))
            holder.tvStatus.setIconResource(R.drawable.panding_clock)
            holder.tvStatus.iconTint = ColorStateList.valueOf(activity.resources.getColor(R.color.black))
        } else if (report.trip_status == "1") {
            holder.tvStatus.text = "Approved"
            holder.tvStatus.setBackgroundColor(activity.resources.getColor(R.color.green))
            holder.tvStatus.setIconResource(R.drawable.verified_icon)
            holder.tvStatus.iconTint = ColorStateList.valueOf(activity.resources.getColor(R.color.white))
        } else if (report.trip_status == "2") {
            holder.tvStatus.text = "Rejected"
            holder.tvStatus.setBackgroundColor(activity.resources.getColor(R.color.red))
            holder.tvStatus.setIconResource(R.drawable.rejected_icon)
            holder.tvStatus.iconTint = ColorStateList.valueOf(activity.resources.getColor(R.color.white))
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



        holder.llDelete.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("Delete Trip")
                .setMessage("Are you sure you want to delete this trip?")
                .setPositiveButton("Yes") { dialog, which ->
                    deleteTrip(report.id)
                }
                .setNegativeButton("No", null)
                .show()
        }


        // Load the image and adjust the height based on its aspect ratio
        // Load the image and adjust the height based on its aspect ratio
        Glide.with(activitys)
            .asBitmap()
            .load(report.trip_image)
            .placeholder(R.drawable.placeholder_bg)
            .into(object : com.bumptech.glide.request.target.BitmapImageViewTarget(holder.ivProfileImage) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    // Calculate the new dimensions based on the image aspect ratio
                    val width = resource.width / 1
                    val height = resource.height / 1

                    // Set the ImageView's dimensions
                    holder.ivProfileImage.layoutParams.width = width
                    holder.ivProfileImage.layoutParams.height = height
                    holder.ivProfileImage.requestLayout()

                    // Set the loaded bitmap to the ImageView
                    holder.ivProfileImage.setImageBitmap(resource)
                }
            })







//                    Glide.with(activitys).load(report.trip_image).placeholder(R.drawable.placeholder_bg)
//            .into(holder.ivProfileImage)

        Glide.with(activitys).load(report.profile).placeholder(R.drawable.profile_placeholder)
            .into(holder.ivProfile)


    }

    fun deleteTrip(id: String?) {
        val params: MutableMap<String, String> = HashMap()
        params["trip_id"] = id!!
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {



                        (activity as MytripsActivity).binding.swipeRefreshLayout.isRefreshing = true
                        // call the fuction MytripsActivity.mytripList()
                        (activity as MytripsActivity).mytripList()

                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()


                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            // Stop the refreshing animation once the network request is complete

        }, activity, Constant.DELETE_TRIP, params, true, 1)



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
//        val rlStatus:RelativeLayout = itemView.findViewById(R.id.rlStatus)
        val tvStatus:MaterialButton = itemView.findViewById(R.id.tvStatus)
        val llDelete: MaterialButton = itemView.findViewById(R.id.llDelete)





    }


}