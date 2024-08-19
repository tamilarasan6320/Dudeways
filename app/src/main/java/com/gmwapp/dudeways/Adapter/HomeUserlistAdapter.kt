package com.gmwapp.dudeways.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
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
import com.gmwapp.dudeways.Activity.ChatsActivity
import com.gmwapp.dudeways.Model.HomeUserlist
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.ApiConfig.Companion.session
import com.google.android.material.button.MaterialButton
import org.json.JSONException
import org.json.JSONObject
import com.gmwapp.dudeways.helper.Session

class HomeUserlistAdapter(
    val activity: Activity,
    homeUserlist: java.util.ArrayList<HomeUserlist>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val homeUserlist: ArrayList<HomeUserlist>
    val activitys: Activity

    val session = Session(activity)

    init {
        this.homeUserlist = homeUserlist
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.layout_home_userlist, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: HomeUserlist = homeUserlist[position]








        Glide.with(activitys).load(report.profile).placeholder(R.drawable.placeholder_bg).into(holder.ivProfile)


        holder.ivProfile.setOnClickListener {
            if (report.id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", report.id)
                intent.putExtra("name", report.name)
                session.setData("reciver_profile", report.profile)
                intent.putExtra("chat_user_id", report.id)
                intent.putExtra("unique_name", report.unique_name)
                activity.startActivity(intent)
            }
        }
    }


    override fun getItemCount(): Int {
        return homeUserlist.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


 val ivProfile = itemView.findViewById<ImageView>(R.id.ivProfile)
        val ivVerify = itemView.findViewById<ImageView>(R.id.ivVerify)



    }


}