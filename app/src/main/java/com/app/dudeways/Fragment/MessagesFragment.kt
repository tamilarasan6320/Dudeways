package com.app.dudeways.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.dudeways.Activity.HomeActivity
import com.app.dudeways.Activity.ProfileViewActivity
import com.app.dudeways.Adapter.ChatlistAdapter
import com.app.dudeways.Adapter.ConnectAdapter
import com.app.dudeways.Model.Chatlist
import com.app.dudeways.Model.Connect
import com.app.dudeways.databinding.FragmentMessagesBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject


class MessagesFragment : Fragment() {

    lateinit var binding: FragmentMessagesBinding
    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessagesBinding.inflate(inflater, container, false)


        activity = requireActivity()

        (activity as HomeActivity).binding.rltoolbar.visibility = View.VISIBLE


        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvChat.layoutManager = linearLayoutManager


        chatlist()

        return binding.root

    }


    private fun chatlist() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val g = Gson()
                        val chat_list = ArrayList<Chatlist>()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            if (jsonObject1 != null) {
                                val connect = g.fromJson(jsonObject1.toString(), Chatlist::class.java)
                                chat_list.add(connect)
                            }
                        }


                        val chatlistAdapter = ChatlistAdapter(requireActivity(),chat_list)
                        binding.rvChat.adapter = chatlistAdapter
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

        }, activity, Constant.CHAT_LIST, params, true, 1)


    }



}