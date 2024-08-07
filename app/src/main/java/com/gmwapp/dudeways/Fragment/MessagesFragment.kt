package com.gmwapp.dudeways.Fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Activity.HomeActivity
import com.gmwapp.dudeways.Adapter.ChatlistAdapter
import com.gmwapp.dudeways.Model.Chatlist
import com.gmwapp.dudeways.databinding.FragmentMessagesBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class MessagesFragment : Fragment() {
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var activity: Activity
    private lateinit var session: Session
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
        binding = FragmentMessagesBinding.inflate(inflater, container, false)
        activity = requireActivity()
        session = Session(activity)

        (activity as HomeActivity).binding.rltoolbar.visibility = View.VISIBLE

        setupRecyclerView()
        setupSwipeRefreshLayout()

        if (chatList.isEmpty()) {
            chatlist()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvChat.layoutManager = linearLayoutManager

        chatlistAdapter = ChatlistAdapter(activity, chatList)
        binding.rvChat.adapter = chatlistAdapter

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
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            chatlist()
        }
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

    override fun onResume() {
        super.onResume()
        // Clear the chat list and reset the offset
        //chatList.clear()
        offset = 0

        // Refresh the chat list
        chatlist()

        // Display a toast message for debugging purposes
     //   Toast.makeText(activity, "onResume", Toast.LENGTH_SHORT).show()
    }


    override fun onStart() {
        super.onStart()
        if (isVisible) {
            // Clear the chat list and reset the offset
        //    chatList.clear()
            offset = 0

            // Refresh the chat list
            chatlist()

            // Display a toast message for debugging purposes
         //   Toast.makeText(activity, "Fragment is visible", Toast.LENGTH_SHORT).show()
        }
    }


}
