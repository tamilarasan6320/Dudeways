package com.app.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.dudeways.Adapter.NotificationAdapter
import com.app.dudeways.Model.Notification
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityNotificationBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class NotificationActivity : AppCompatActivity() {

    lateinit var binding: ActivityNotificationBinding
    lateinit var activity: Activity
    lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        activity = this
        session = Session(activity)
        setContentView(binding.root)

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvNotificationList.layoutManager = linearLayoutManager

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        NotificationList()


    }


    private fun NotificationList() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val g = Gson()
                        val notification = ArrayList<Notification>()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            if (jsonObject1 != null) {
                                val connect = g.fromJson(jsonObject1.toString(), Notification::class.java)
                                notification.add(connect)
                            }
                        }

                        val notificationAdapter = NotificationAdapter(activity,notification)
                        binding.rvNotificationList.adapter = notificationAdapter
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

            // Stop the refreshing animation once the network request is complete
//            binding.swipeRefreshLayout.isRefreshing = false
        }, activity, Constant.NOTFICATION_LIST, params, true, 1)
    }
}