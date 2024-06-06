package com.app.dudeways.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.dudeways.activity.HomeActivity
import com.app.dudeways.Adapter.ConnectAdapter
import com.app.dudeways.Model.Connect
import com.app.dudeways.databinding.FragmentInterestBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject


class InterestFragment : Fragment() {


    lateinit var binding : FragmentInterestBinding
    lateinit var activity : Activity
    lateinit var session : Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInterestBinding.inflate(inflater, container, false)
        activity = requireActivity()

        session = Session(activity)


        (activity as HomeActivity).binding.rltoolbar.visibility = View.VISIBLE


        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvConnectList.layoutManager = linearLayoutManager


        NotificationList()

        return binding.root

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
                        val connect_list = ArrayList<Connect>()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            if (jsonObject1 != null) {
                                val connect = g.fromJson(jsonObject1.toString(), Connect::class.java)
                                connect_list.add(connect)
                            }
                        }

                        val connectAdapter = ConnectAdapter(requireActivity(),connect_list)
                        binding.rvConnectList.adapter = connectAdapter
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
        }, activity, Constant.FREINDS_LIST, params, true, 1)
    }




}