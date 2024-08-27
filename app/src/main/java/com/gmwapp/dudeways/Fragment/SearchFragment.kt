package com.gmwapp.dudeways.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Activity.HomeActivity
import com.gmwapp.dudeways.Adapter.SearchAdapter
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
    private var selectedGender: String? = "all"  // Add gender variable

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
        setupSwipeRefreshLayout()

        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE
        (activity as HomeActivity).binding.bottomNavigationView.visibility = View.GONE
        (activity as HomeActivity).binding.ivSearch.visibility = View.GONE

        setupRecyclerView()

        binding.ivMore.setOnClickListener {
            showPopupMenu()
        }

        return binding.root
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(activity, binding.ivMore)
        popupMenu.inflate(R.menu.filter_menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_male -> {
                    selectedGender = "Male"  // Set gender to Male
                    binding.ivMore.text = "Male"
                    binding.ivMore.setCompoundDrawablesWithIntrinsicBounds(R.drawable.male_ic, 0, 0, 0)
                    binding.ivMore.compoundDrawables[0].setTint(resources.getColor(R.color.blue_200))
                    offset = 0  // Reset offset to 0
                    loadProfileList()  // Call API to load the male profiles
                    true
                }
                R.id.menu_female -> {
                    selectedGender = "Female"  // Set gender to Female
                    binding.ivMore.text = "Female"
                    binding.ivMore.setCompoundDrawablesWithIntrinsicBounds(R.drawable.female_ic, 0, 0, 0)
                    binding.ivMore.compoundDrawables[0].setTint(resources.getColor(R.color.primary))
                    offset = 0  // Reset offset to 0
                    loadProfileList()  // Call API to load the female profiles
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
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

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            loadProfileList()
        }
    }

    override fun onResume() {
        super.onResume()
        handleOnBackPressed()
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
            binding.swipeRefreshLayout.isRefreshing = false
        }, activity, Constant.USERS_LIST, params, true, 1)
    }

    private fun buildProfileParams(): HashMap<String, String> {
        return hashMapOf(
            Constant.USER_ID to session.getData(Constant.USER_ID),
            Constant.OFFSET to offset.toString(),
            Constant.LIMIT to limit.toString(),
            Constant.GENDER to (selectedGender ?: "")  // Add gender parameter if selected
        )
    }

    private fun handleProfileResponse(result: Boolean, response: String) {
        isLoading = false

        if (result) {
            try {
                val jsonObject = JSONObject(response)
                if (jsonObject.getBoolean(Constant.SUCCESS)) {
                    total = jsonObject.getInt(Constant.TOTAL)
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
}
