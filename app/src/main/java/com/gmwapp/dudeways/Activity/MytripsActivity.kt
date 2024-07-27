package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Adapter.MytriplistAdapter
import com.gmwapp.dudeways.Model.Mytriplist
import com.gmwapp.dudeways.databinding.ActivityMytripsBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class MytripsActivity : BaseActivity() {

    lateinit var binding: ActivityMytripsBinding
    lateinit var activity: Activity
    lateinit var session: Session
    private var offset = 0
    private val limit = 10
    private var isLoading = false
    private var total = 0
    private val mytriplist = ArrayList<Mytriplist>()
    private lateinit var mytriplistAdapter: MytriplistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMytripsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvMytriplist.layoutManager = linearLayoutManager

        mytriplistAdapter = MytriplistAdapter(activity, mytriplist)
        binding.rvMytriplist.adapter = mytriplistAdapter

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            mytripList()
        }

        binding.rvMytriplist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    mytripList()
                }
            }
        })

        mytripList()
    }

    fun mytripList() {
        if (isLoading) return
        isLoading = true

        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.OFFSET] = offset.toString()
        params[Constant.LIMIT] = limit.toString()

        ApiConfig.RequestToVolley({ result, response ->
            isLoading = false
            binding.swipeRefreshLayout.isRefreshing = false

            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        total = jsonObject.getInt(Constant.TOTAL)
                        if (offset == 0) {
                            mytriplist.clear()
                        }

                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val gson = Gson()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val trip = gson.fromJson(jsonObject1.toString(), Mytriplist::class.java)
                            mytriplist.add(trip)
                        }

                        mytriplistAdapter.notifyDataSetChanged()
                        offset += limit
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
        }, activity, Constant.MY_TRIP_LIST, params, true, 1)
    }
}
