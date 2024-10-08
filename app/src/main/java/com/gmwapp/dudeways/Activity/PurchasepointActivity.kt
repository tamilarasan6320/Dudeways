package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityPurchasepointBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.androidbrowserhelper.trusted.LauncherActivity
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

//                val intent = Intent(activity, MainActivity::class.java)
//                intent.putExtra("id", item.id)
//                session.setData(Constant.AMOUNT,item.price)
//                startActivity(intent)

            }

            holder.itemView.setOnClickListener {
                showMobileInputDialog(item.id, item.price)
            }

        }

        private fun showMobileInputDialog(pointsId: String, amount: String) {
            // Inflate custom dialog layout
            val dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_mobile_dialog, null)

            // Find the views
            val etMobileNumber: EditText = dialogView.findViewById(R.id.etMobileNumber)
            val btnSubmit: Button = dialogView.findViewById(R.id.btnSubmit)
            val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)

            // Create AlertDialog with the custom layout
            val dialog = AlertDialog.Builder(activity)
                .setView(dialogView)
                .create()

            // Set click listener for submit button
            btnSubmit.setOnClickListener {
                val mobileNumber = etMobileNumber.text.toString().trim()

                // Validate the mobile number
                if (mobileNumber.isEmpty()) {
                    Toast.makeText(activity, "Please enter mobile number", Toast.LENGTH_SHORT).show()
                } else if (mobileNumber.length != 10) {
                    Toast.makeText(activity, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show()
                } else {
                    session.setData(Constant.POINT_PAYMENT_MOBILE, mobileNumber)
                    initiatePaymentLink(pointsId, amount)
                    dialog.dismiss() // Dismiss the dialog after submitting
                }
            }

            // Set click listener for cancel button
            btnCancel.setOnClickListener {
                dialog.dismiss() // Dismiss the dialog when cancelled
            }

            // Show the dialog
            dialog.show()
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

    private fun initiatePaymentLink(pointsId: String, amount: String, ) {
        val params = HashMap<String, String>().apply {
            put("buyer_name", session.getData(Constant.NAME))
            put("amount", amount)
            put("email", session.getData(Constant.EMAIL))
            put("phone", session.getData(Constant.POINT_PAYMENT_MOBILE))
            put("purpose", session.getData(Constant.USER_ID) + "-" + pointsId)
        }

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    Log.d("FULL_RESPONSE", response)
                    val jsonObject = JSONObject(response)
                    val longUrl = jsonObject.getString("longurl")
                    val intent= Intent(activity, LauncherActivity::class.java)
                    intent.setData(Uri.parse(longUrl))
                    startActivity(intent)
                    finish()// Directly starting the intent without launcher

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "JSON Parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.ADD_POINTS_REQUEST, params, true)

        Log.d("ADD_POINTS_REQUEST","ADD_POINTS_REQUEST: " + Constant.ADD_POINTS_REQUEST)
        Log.d("ADD_POINTS_REQUEST","ADD_POINTS_REQUESTparams: " + params)
    }
}
