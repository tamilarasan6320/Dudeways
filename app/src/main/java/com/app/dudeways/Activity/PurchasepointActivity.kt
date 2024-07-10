package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityPurchasepointBinding
import com.app.dudeways.gateway.MainActivity
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject

class PurchasepointActivity : BaseActivity() {

    lateinit var binding: ActivityPurchasepointBinding
    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasepointBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager

        purchase()
    }

    private fun purchase() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val purchaseList = ArrayList<PurchaseItem>()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            if (jsonObject1 != null) {


                               val id = jsonObject1.getString("id")
                                val points = jsonObject1.getString("points")
                                val offer_percentage = jsonObject1.getString("offer_percentage")
                                val price = jsonObject1.getString("price")
                                val datetime = jsonObject1.getString("datetime")
                                val updated_at = jsonObject1.getString("updated_at")
                                val created_at = jsonObject1.getString("created_at")


                                purchaseList.add(PurchaseItem(points, offer_percentage, price,id))
                            }
                        }

                        val purchaseAdapter = PurchaseAdapter(activity, purchaseList)
                        binding.recyclerView.adapter = purchaseAdapter
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
        }, activity, Constant.POINTS_LIST, params, true, 1)
    }
    private fun addpurchase(id: String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params["points_id"] = id
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)

                        val total_points = jsonobj.getString("total_points")


                        Toast.makeText(activity, "You have earned " + total_points + " points", Toast.LENGTH_SHORT).show()

                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            else {
                Toast.makeText(activity, response, Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.ADD_POINTS, params, true, 1)
    }

    data class PurchaseItem(val title: String, val percentage: String, val price: String, val id: String)

    inner class PurchaseAdapter(private val activity: Activity, private val list: ArrayList<PurchaseItem>) : RecyclerView.Adapter<PurchaseAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_purchase, parent, false)
            return ItemHolder(v)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val item = list[position]
            holder.tvTitle.text ="Get "  +  item.title   + " Points"

            if (item.percentage == "0") {
                holder.tvPercentage.visibility = View.GONE
            } else {
                holder.tvPercentage.visibility = View.VISIBLE
                holder.tvPercentage.text = item.percentage + "% OFF"
            }


            holder.tvPrice.text = "₹" + item.price

            holder.itemView.setOnClickListener {
//                addpurchase(item.id)

                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("id", item.id)
                session.setData(Constant.AMOUNT,item.price)
                startActivity(intent)

            }

        }

        override fun getItemCount(): Int {
            return list.size
        }

        inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
            var tvPercentage: TextView = itemView.findViewById(R.id.tvPercentage)
            var tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        }
    }
}
