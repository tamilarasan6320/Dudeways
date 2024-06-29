package com.app.dudeways.Fragment

import android.app.Activity
import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.dudeways.Activity.HomeActivity
import com.google.gson.Gson
import com.app.dudeways.Adapter.HomePtofilesAdapter
import com.app.dudeways.Model.HomeCategory
import com.app.dudeways.Model.HomeProfile
import com.app.dudeways.R
import com.app.dudeways.databinding.FragmentHomeBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeCategoryAdapter: HomeCategorysAdapter
    private val homeCategoryList = ArrayList<HomeCategory>()
    private val homeProfileList = ArrayList<HomeProfile>()
    private lateinit var homeProfilesAdapter: HomePtofilesAdapter
    private lateinit var activity: Activity
    private lateinit var session: Session
    private var selectedItemPosition = 0 // Set default position to 0
    private var offset = 0
    private val limit = Constant.LOAD_ITEM_LIMIT
    private var isLoading = false
    private var currentType: String = "latest"
    private var selectedDated: String? = null
    private var formattedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        activity = requireActivity()
        session = Session(activity)

        (activity as HomeActivity).binding.rltoolbar.visibility = View.VISIBLE

        setupRecyclerViews()
        categoryList()
        ProfileList("nearby")

        binding.swipeRefreshLayout.setOnRefreshListener {
            when (selectedItemPosition) {
                0 -> ProfileList("nearby")
                1 -> ProfileList("latest")
                2 -> ProfileList("date")
            }
        }

        return binding.root
    }

    private fun setupRecyclerViews() {
        // Setup rvCategoryList with GridLayoutManager
        binding.rvCategoryList.layoutManager = GridLayoutManager(activity, 3)

        // Setup rvProfileList with LinearLayoutManager
        binding.rvProfileList.layoutManager = LinearLayoutManager(activity)

        // Initialize adapters
        homeCategoryAdapter = HomeCategorysAdapter(activity, homeCategoryList)
        homeProfilesAdapter = HomePtofilesAdapter(activity, homeProfileList)

        // Set adapters to RecyclerViews
        binding.rvCategoryList.adapter = homeCategoryAdapter
        binding.rvProfileList.adapter = homeProfilesAdapter

        // Add scroll listener to rvProfileList for pagination
        binding.rvProfileList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1) {
                    ProfileList(currentType)
                }
            }
        })
    }

    private fun categoryList() {
        val homeCategory = ArrayList<HomeCategory>()
        val cat1 = HomeCategory("1", "Nearby", "")
        val cat2 = HomeCategory("1", "Latest", "")
        val cat3 = HomeCategory("1", "Trip Date", "")

        homeCategory.add(cat1)
        homeCategory.add(cat2)
        homeCategory.add(cat3)

        val homeCategoryAdapter = HomeCategorysAdapter(requireActivity(), homeCategory)
        binding.rvCategoryList.adapter = homeCategoryAdapter
    }

    private fun ProfileList(type: String) {
        if (isLoading) return
        isLoading = true
        val params = HashMap<String, String>()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.TYPE] = "latest"
        params[Constant.OFFSET] = offset.toString()
        params[Constant.LIMIT] = limit.toString()
        params["date"] = selectedDated ?: ""
        ApiConfig.RequestToVolley({ result, response ->
            isLoading = false
            binding.swipeRefreshLayout.isRefreshing = false
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val gson = Gson()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val profile = gson.fromJson(jsonObject1.toString(), HomeProfile::class.java)
                            homeProfileList.add(profile)
                        }

                        if (offset == 0) {
                            homeProfilesAdapter.notifyDataSetChanged()
                        } else {
                            homeProfilesAdapter.notifyItemRangeInserted(offset, homeProfileList.size - 1)
                        }
                        offset += limit
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.TRIP_LIST, params, true, 1)
    }



    inner class HomeCategorysAdapter(private val activity: Activity, private val homeCategoryList: ArrayList<HomeCategory>) : RecyclerView.Adapter<HomeCategorysAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_home_category, parent, false)
            return ItemHolder(v)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val category = homeCategoryList[position]

            holder.tvName.text = if (position == 2 && selectedDated != null) {
                selectedDated
            } else {
                category.name
            }

            if (selectedItemPosition == position) {
                holder.cardView.setCardBackgroundColor(activity.resources.getColor(R.color.primary))
                holder.tvName.setTextColor(activity.resources.getColor(R.color.white))
                holder.tvName.setTypeface(null, Typeface.BOLD)
            } else {
                holder.cardView.setCardBackgroundColor(activity.resources.getColor(R.color.white))
                holder.tvName.setTextColor(activity.resources.getColor(R.color.black))
                holder.tvName.setTypeface(null, Typeface.NORMAL)
            }

            holder.cardView.setOnClickListener {
                val previousPosition = selectedItemPosition
                selectedItemPosition = position
                notifyItemChanged(previousPosition)
                notifyItemChanged(position)
                if (position == 0) {
                    ProfileList("nearby")
                } else if (position == 1) {
                    ProfileList("latest")
                } else if (position == 2) {
                    showDatePickerDialog(holder.tvName)
                }
            }
        }

        override fun getItemCount(): Int {
            return homeCategoryList.size
        }

        inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvName: TextView = itemView.findViewById(R.id.tvName)
            val cardView: CardView = itemView.findViewById(R.id.cardView)
        }

        private fun showDatePickerDialog(tvName: TextView) {
            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
            dialog.setContentView(R.layout.dialog_calendar)
            dialog.setCanceledOnTouchOutside(true)

            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val calendarView = dialog.findViewById<CalendarView>(R.id.calendarView)
            calendarView.minDate = System.currentTimeMillis()

            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                formattedDate = dateFormat.format(selectedDate.time)

                tvName.text = formattedDate
                selectedDated = formattedDate // Store the selected date

                // call notifyDataSetChanged()
                notifyDataSetChanged()
                ProfileList("date")

                dialog.dismiss()
            }

            dialog.show()
        }

    }
}
