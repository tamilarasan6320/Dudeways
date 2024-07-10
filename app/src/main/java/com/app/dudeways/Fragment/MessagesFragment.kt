package com.app.dudeways.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.dudeways.Activity.HomeActivity
import com.app.dudeways.Adapter.ChatlistAdapter
import com.app.dudeways.Model.Chatlist
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
    private var offset = 0
    private val limit = 10
    private var isLoading = false
    private var total = 0
    private val chatList = ArrayList<Chatlist>()
    private lateinit var chatlistAdapter: ChatlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessagesBinding.inflate(inflater, container, false)

        activity = requireActivity()
        session = Session(activity)

        (activity as HomeActivity).binding.rltoolbar.visibility = View.VISIBLE

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvChat.layoutManager = linearLayoutManager

        chatlistAdapter = ChatlistAdapter(activity, chatList)
        binding.rvChat.adapter = chatlistAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            chatlist()
        }

        binding.rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    chatlist()
                }
            }
        })

        chatlist()

        return binding.root
    }

    private fun chatlist() {
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
                            chatList.clear()
                        }

                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val gson = Gson()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val chatItem = gson.fromJson(jsonObject1.toString(), Chatlist::class.java)
                            chatList.add(chatItem)
                        }

                        chatlistAdapter.notifyDataSetChanged()
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
        }, activity, Constant.CHAT_LIST, params, true, 1)
    }
}
