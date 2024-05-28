package gmw.app.dudeways.Adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import gmw.app.dudeways.Activity.ProfileinfoActivity
import gmw.app.dudeways.Model.Connect
import gmw.app.dudeways.R

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


        holder.itemView.setOnClickListener{
            val intent = Intent(activity, ProfileinfoActivity::class.java)
            activity.startActivity(intent)

        }


//        holder.tvName.text = report.name


    }


    override fun getItemCount(): Int {
        return connect.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        val tvName: TextView = itemView.findViewById(R.id.tvName)


    }


}