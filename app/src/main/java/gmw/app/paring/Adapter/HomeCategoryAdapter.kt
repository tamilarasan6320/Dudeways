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
import gmw.app.paring.R
import org.json.JSONException

class HomeCategoryAdapter(
    val activity: Activity,
    homeCategory: java.util.ArrayList<HomeCategory>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val homeCategory: ArrayList<HomeCategory>
    val activitys: Activity

    init {
        this.homeCategory = homeCategory
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.layout_home_category, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: HomeCategory = homeCategory[position]


        holder.tvName.text = report.name


    }


    override fun getItemCount(): Int {
        return homeCategory.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView = itemView.findViewById(R.id.tvName)


    }


}