package com.gmwapp.dudeways.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Activity.MytripsActivity
import com.gmwapp.dudeways.Model.Mytriplist
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.bumptech.glide.Glide
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


        if (report.trip_status == "1") {
            holder.tvStatus.text = "Approved"
            holder.rlStatus.setBackgroundColor(ContextCompat.getColor(activity, R.color.green))
        }
        else if (report.trip_status == "2") {
            holder.tvStatus.text = "Rejected"
            holder.rlStatus.setBackgroundColor(ContextCompat.getColor(activity, R.color.red))
        }
        else {
            holder.tvStatus.text = "Pending"
            holder.rlStatus.setBackgroundColor(ContextCompat.getColor(activity, R.color.blue_200))
        }



        holder.tvName.text = report.name
        holder.tvLocation.text = report.location
        holder.tvDescription.text = report.trip_description
        holder.tvUsername.text = "@" + report.unique_name
        holder.tvDate.text = "From " + report.from_date + " to " + report.to_date
        holder.tvTitle.text = report.trip_title

        // Load the image and adjust the height based on its aspect ratio
        Glide.with(activitys)
            .asBitmap()
            .load(report.trip_image)
            .placeholder(R.drawable.placeholder_bg)
            .into(object : com.bumptech.glide.request.target.BitmapImageViewTarget(holder.ivProfileImage) {
                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    // Calculate the height based on the image aspect ratio
                    val width = holder.ivProfileImage.width
                    val aspectRatio = resource.height.toFloat() / resource.width.toFloat()
                    val height = (width * aspectRatio).toInt()

                    // Set the ImageView's height
                    holder.ivProfileImage.layoutParams.height = height
                    holder.ivProfileImage.requestLayout()

                    // Set the loaded bitmap to the ImageView
                    holder.ivProfileImage.setImageBitmap(resource)
                }
            })

        Glide.with(activitys).load(report.profile).placeholder(R.drawable.profile_placeholder)
            .into(holder.ivProfile)

        // Other binding logic...
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
        val rlStatus:RelativeLayout = itemView.findViewById(R.id.rlStatus)
        val tvStatus:TextView = itemView.findViewById(R.id.tvStatus)
        val llDelete:LinearLayout = itemView.findViewById(R.id.llDelete)





    }


}