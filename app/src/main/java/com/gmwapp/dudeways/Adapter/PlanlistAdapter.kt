package com.gmwapp.dudeways.Adapter

import android.app.Activity
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Model.Planlist
import com.gmwapp.dudeways.R

class PlanlistAdapter(
    val activity: Activity,
    private val planList: ArrayList<Planlist>,
    private val onPlanSelected: (Planlist) -> Unit
) : RecyclerView.Adapter<PlanlistAdapter.ItemHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.layout_planlist, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val plan = planList[position]

        holder.tvSave.text = "Save Rs." + plan.save_amount
        holder.tvPlan.text = plan.plan_name
        holder.tvDays.text = "${plan.validity} Days"
        holder.tvPlanPrice.text = "Rs.${plan.price}"

        holder.rbplan.isChecked = position == selectedPosition
        holder.cvPlan.setCardBackgroundColor(
            if (position == selectedPosition) getColorFromAttr(R.attr.cardlightcolor)
            else getColorFromAttr(R.attr.cardbgcolor)
        )

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onPlanSelected(plan)
        }

        holder.rbplan.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onPlanSelected(plan)
        }
    }

    override fun getItemCount(): Int {
        return planList.size
    }

    private fun getColorFromAttr(attr: Int): Int {
        val typedValue = TypedValue()
        activity.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSave: TextView = itemView.findViewById(R.id.tvSave)
        val tvPlan: TextView = itemView.findViewById(R.id.tvPlan)
        val tvDays: TextView = itemView.findViewById(R.id.tvDays)
        val tvPlanPrice: TextView = itemView.findViewById(R.id.tvPlanPrice)
        val rbplan: RadioButton = itemView.findViewById(R.id.rbplan)
        val cvPlan: CardView = itemView.findViewById(R.id.cvPlan)
    }
}
