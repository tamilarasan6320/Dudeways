package gmw.app.paring.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import gmw.app.paring.Model.HomeCategory
import gmw.app.paring.Model.HomeProfile
import gmw.app.paring.Model.Notification
import gmw.app.paring.R
import org.json.JSONException

class NotificationAdapter(
    val activity: Activity,
    notification: java.util.ArrayList<Notification>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val notification: ArrayList<Notification>
    val activitys: Activity

    init {
        this.notification = notification
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.layout_home_notification, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: Notification = notification[position]


//        holder.tvName.text = report.name


    }


    override fun getItemCount(): Int {
        return notification.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        val tvName: TextView = itemView.findViewById(R.id.tvName)


    }


}