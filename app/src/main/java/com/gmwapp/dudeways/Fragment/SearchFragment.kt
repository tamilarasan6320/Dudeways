package com.gmwapp.dudeways.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Activity.HomeActivity
import com.gmwapp.dudeways.Adapter.ConnectAdapter
import com.gmwapp.dudeways.Adapter.SearchAdapter
import com.gmwapp.dudeways.Model.Connect
import com.gmwapp.dudeways.Model.HomeProfile
import com.gmwapp.dudeways.Model.UsersList
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.FragmentSearchBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var activity: Activity
    private lateinit var session: Session

    private lateinit var searchAdapter: SearchAdapter

    private val usersList = ArrayList<UsersList>()
    private var isLoading = false
    private var total = 0
    private var offset = 0
    private val limit = 10

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        activity = requireActivity()
        session = Session(activity)

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        if (usersList.isEmpty()) {
            loadProfileList()
        }

//        usersList()

        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE
        (activity as HomeActivity).binding.bottomNavigationView.visibility = View.GONE
        (activity as HomeActivity).binding.ivSearch.visibility = View.GONE

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvSearch.layoutManager = linearLayoutManager

        searchAdapter = SearchAdapter(activity, usersList)
        binding.rvSearch.adapter = searchAdapter

        binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    loadProfileList()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        handleOnBackPressed()
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Replace the current fragment with HomeFragment
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            }
        })
    }

    private fun loadProfileList() {
        if (isLoading) return
        isLoading = true

        val params = buildProfileParams()
        ApiConfig.RequestToVolley({ result, response ->
            handleProfileResponse(result, response)
        }, activity, Constant.USERS_LIST, params, true, 1)
    }

    private fun buildProfileParams(): java.util.HashMap<String, String> {
        return hashMapOf(
            Constant.USER_ID to session.getData(Constant.USER_ID),
            Constant.OFFSET to offset.toString(),
            Constant.LIMIT to limit.toString(),
        )
    }

    private fun handleProfileResponse(result: Boolean, response: String) {
        isLoading = false

        if (result) {
            try {
                val jsonObject = JSONObject(response)
                if (jsonObject.getBoolean(Constant.SUCCESS)) {
                    total = jsonObject.getInt(Constant.TOTAL)
//                    if (offset == 0) {
//                        usersList.clear()
//                    }

//                    val jsonArray = jsonObject.getJSONArray(Constant.DATA)
//                    val gson = Gson()
//
//                    for (i in 0 until jsonArray.length()) {
//                        val jsonObject1 = jsonArray.getJSONObject(i)
//                        val users = gson.fromJson(jsonObject1.toString(), UsersList::class.java)
//                        usersList.add(users)
//                    }

                    searchAdapter.notifyDataSetChanged()
                    updateProfileList(jsonObject)
                } else {
                    Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateProfileList(jsonObject: JSONObject) {
        binding.rvSearch.visibility = View.VISIBLE

        if (offset == 0) {
            usersList.clear()
        }

        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
        val gson = Gson()

        for (i in 0 until jsonArray.length()) {
            val users = gson.fromJson(jsonArray.getJSONObject(i).toString(), UsersList::class.java)
            usersList.add(users)
        }

        if (offset == 0) {
            searchAdapter.notifyDataSetChanged()
        } else {
            searchAdapter.notifyItemRangeInserted(offset, usersList.size - 1)
        }

        offset += limit
    }

//    private fun usersList() {
//        if (isLoading) return
//        isLoading = true
//
//        val params: MutableMap<String, String> = HashMap()
//        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
//        Constant.OFFSET to offset.toString()
//
//        ApiConfig.RequestToVolley({ result, response ->
//            isLoading = false
//
//            if (result) {
//                try {
//                    val jsonObject = JSONObject(response)
//                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
//                        total = jsonObject.getInt(Constant.TOTAL)
//                        if (offset == 0) {
//                            usersList.clear()
//                        }
//
//                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
//                        val gson = Gson()
//
//                        for (i in 0 until jsonArray.length()) {
//                            val jsonObject1 = jsonArray.getJSONObject(i)
//                            val users = gson.fromJson(jsonObject1.toString(), UsersList::class.java)
//                            usersList.add(users)
//                        }
//
//                        searchAdapter.notifyDataSetChanged()
//                    } else {
//                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            }
//        }, activity, Constant.USERS_LIST, params, true, 1)
//    }
}
