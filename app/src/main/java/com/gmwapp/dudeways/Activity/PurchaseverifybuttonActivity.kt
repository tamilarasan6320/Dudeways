package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmwapp.dudeways.Adapter.PlanlistAdapter
import com.gmwapp.dudeways.Model.Planlist
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityPurchaseverifybuttonBinding
import com.gmwapp.dudeways.gateway.MainActivity
import com.gmwapp.dudeways.gateway.PurchaseVerifyActivity
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PurchaseverifybuttonActivity : BaseActivity() {

    lateinit var binding: ActivityPurchaseverifybuttonBinding
    lateinit var activity: Activity
    lateinit var session: Session
    lateinit var amount:String
    lateinit var id:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseverifybuttonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)


        binding.rvplan.layoutManager = LinearLayoutManager(activity)

        binding.ivBack.setOnClickListener {
            onBackPressed()
            finish()
        }

        binding.btnPurchase.setOnClickListener{
            val intent = Intent(activity, PurchaseVerifyActivity::class.java)
            intent.putExtra("id", id)
            session.setData(Constant.AMOUNT,amount)
            startActivity(intent)
        }

        // Call the API to fetch the plan list
        planlist()
    }

    private fun planlist() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray: JSONArray = jsonObject.getJSONArray(Constant.DATA)
                        val gson = Gson()
                        val planList: ArrayList<Planlist> = ArrayList()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val plan: Planlist = gson.fromJson(jsonObject1.toString(), Planlist::class.java)
                            planList.add(plan)
                        }

                        // Set up the RecyclerView with the adapter
                        val adapter = PlanlistAdapter(activity, planList) { plan ->
                        //    showToast("${plan.plan_name} selected")

                            amount = plan.price.toString()
                            id = plan.id.toString()


                        }
                        binding.rvplan.adapter = adapter

                    } else {
                        Toast.makeText(activity, "Error: ${jsonObject.getString(Constant.MESSAGE)}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "JSON error: ${e.toString()}", Toast.LENGTH_SHORT).show()
                }
            }
        }, activity, Constant.PLAN_LIST, params, true)
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}
