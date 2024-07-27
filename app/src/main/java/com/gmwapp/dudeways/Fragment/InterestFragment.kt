package com.gmwapp.dudeways.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Activity.HomeActivity
import com.gmwapp.dudeways.Adapter.ConnectAdapter
import com.gmwapp.dudeways.Model.Connect
import com.gmwapp.dudeways.databinding.FragmentInterestBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class InterestFragment : Fragment() {

    private lateinit var binding: FragmentInterestBinding
    private lateinit var activity: Activity
    private lateinit var session: Session
    private var offset = 0
    private val limit = 10
    private var isLoading = false
    private var total = 0
    private val connectList = ArrayList<Connect>()
    private lateinit var connectAdapter: ConnectAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInterestBinding.inflate(inflater, container, false)
        activity = requireActivity()
        session = Session(activity)

        (activity as HomeActivity).binding.rltoolbar.visibility = View.VISIBLE

        setupRecyclerView()
        setupSwipeRefreshLayout()

        if (connectList.isEmpty()) {
            NotificationList()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvConnectList.layoutManager = linearLayoutManager

        connectAdapter = ConnectAdapter(activity, connectList)
        binding.rvConnectList.adapter = connectAdapter

        binding.rvConnectList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    NotificationList()
                }
            }
        })
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            NotificationList()
        }
    }

    private fun NotificationList() {
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
                            connectList.clear()
                        }

                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val gson = Gson()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val connect = gson.fromJson(jsonObject1.toString(), Connect::class.java)
                            connectList.add(connect)
                        }

                        connectAdapter.notifyDataSetChanged()
                        offset += limit
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.FREINDS_LIST, params, true, 1)
    }
}
