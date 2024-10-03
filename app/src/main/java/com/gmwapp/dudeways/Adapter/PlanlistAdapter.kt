package com.gmwapp.dudeways.Adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Model.Planlist
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.google.androidbrowserhelper.trusted.LauncherActivity
import org.json.JSONException
import org.json.JSONObject


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
        holder.tvDays.text = " / ${plan.validity} Days"
        holder.tvPlanPrice.text = "Rs.${plan.price}"

        holder.rbplan.visibility = View.GONE

        // On plan item click, handle selection and initiate payment
        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onPlanSelected(plan)
            initiatePaymentLink()
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

    private fun initiatePaymentLink() {
        val params = HashMap<String, String>().apply {
            put("buyer_name", "tamil")
            put("amount", "10.00")
            put("email", "test@gmail.com")
            put("phone", "6382088746")
            put("purpose", "1")
        }

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    Log.d("FULL_RESPONSE", response)
                    val jsonObject = JSONObject(response)
                    val longUrl = jsonObject.getString("longurl")
                    val intent= Intent(activity, LauncherActivity::class.java)
                    intent.setData(Uri.parse(longUrl))
                    activity.startActivity(intent) // Directly starting the intent without launcher

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "JSON Parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.PAYMENT_LINK, params, true)
    }
}
