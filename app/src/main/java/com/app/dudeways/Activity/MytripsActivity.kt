package com.app.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.dudeways.Adapter.MytriplistAdapter
import com.app.dudeways.Adapter.NotificationAdapter
import com.app.dudeways.Model.Mytriplist
import com.app.dudeways.Model.Notification
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityMytripsBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class MytripsActivity : AppCompatActivity() {

    lateinit var binding: ActivityMytripsBinding
    lateinit var activity: Activity
    lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mytrips)
        binding = ActivityMytripsBinding.inflate(layoutInflater)
        activity = this
        session = Session(activity)


        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvMytriplist.layoutManager = linearLayoutManager

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        mytripList()

        setContentView(binding.root)

    }

    private fun mytripList() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val g = Gson()
                        val mytriplist = ArrayList<Mytriplist>()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            if (jsonObject1 != null) {
                                val connect = g.fromJson(jsonObject1.toString(), Mytriplist::class.java)
                                mytriplist.add(connect)
                            }
                        }

                        val mytriplistAdapter = MytriplistAdapter(activity,mytriplist)
                        binding.rvMytriplist.adapter = mytriplistAdapter
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
        }, activity, Constant.MY_TRIP_LIST, params, true, 1)
    }
}