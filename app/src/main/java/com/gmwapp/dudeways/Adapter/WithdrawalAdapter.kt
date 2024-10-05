package com.gmwapp.dudeways.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Model.WithdrawalList
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.helper.Session

class WithdrawalAdapter(
    val activity: Activity,
    val withdrawalList: ArrayList<WithdrawalList> // Use the correct type here
) : RecyclerView.Adapter<WithdrawalAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.layout_wallet, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val report: WithdrawalList = withdrawalList[position]
        holder.tvPrice.text = "₹${ report.amount.toString() }"
        holder.tvDateTime.text = report.datetime

        if (report.status == 0) {
            holder.tvTitle.text = "Pending"
            holder.tvTitle.setTextColor(activity.resources.getColor(R.color.primary))
            holder.civProfile.setImageResource(R.drawable.pending_img)
        } else if (report.status == 1) {
            holder.tvTitle.text = "Success"
//            holder.tvTitle.setTextColor(activity.resources.getColor(R.color.success_color))
            holder.civProfile.setImageResource(R.drawable.done_ic)
        } else if (report.status == 2) {
            holder.tvTitle.text = "Cancel"
//            holder.tvTitle.setTextColor(activity.resources.getColor(R.color.success_color))
            holder.civProfile.setImageResource(R.drawable.failed_ic)
        }
    }

    override fun getItemCount(): Int {
        return withdrawalList.size
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val civProfile: ImageView = itemView.findViewById(R.id.civProfile)
    }
}

