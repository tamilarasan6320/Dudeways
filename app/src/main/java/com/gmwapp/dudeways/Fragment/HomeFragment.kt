package com.gmwapp.dudeways.Fragment

import android.app.Activity
import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.Activity.HomeActivity
import com.google.gson.Gson
import com.gmwapp.dudeways.Adapter.HomeProfilesAdapter
import com.gmwapp.dudeways.Model.HomeCategory
import com.gmwapp.dudeways.Model.HomeProfile
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.FragmentHomeBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeCategoryAdapter: HomeCategorysAdapter
    private val homeCategoryList = ArrayList<HomeCategory>()
    private val homeProfileList = ArrayList<HomeProfile>()
    private lateinit var homeProfilesAdapter: HomeProfilesAdapter
    private lateinit var activity: Activity
    private lateinit var session: Session
    private var selectedItemPosition = 0
    private var offset = 0
    private val limit = 10
    private var isLoading = false
    private var currentType: String = "nearby"
    private var selectedDate: String? = null
    private var total = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        activity = requireActivity()
        session = Session(activity)

        (activity as HomeActivity).binding.rltoolbar.visibility = View.VISIBLE

        setupRecyclerViews()
        loadCategoryList()

        if (homeProfileList.isEmpty()) {
            loadProfileList("nearby")
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            loadProfileList(
                when (selectedItemPosition) {
                    0 -> "nearby"
                    1 -> "latest"
                    else -> "date"
                }
            )
        }

        return binding.root
    }

    private fun setupRecyclerViews() {
        binding.rvCategoryList.layoutManager = GridLayoutManager(activity, 3)
        binding.rvProfileList.layoutManager = LinearLayoutManager(activity)

        homeCategoryAdapter = HomeCategorysAdapter(activity, homeCategoryList)
        homeProfilesAdapter = HomeProfilesAdapter(activity, homeProfileList)

        binding.rvCategoryList.adapter = homeCategoryAdapter
        binding.rvProfileList.adapter = homeProfilesAdapter

        binding.rvProfileList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    loadProfileList(currentType)
                }
            }
        })
    }

    private fun loadCategoryList() {
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

    private fun loadProfileList(type: String) {
        if (isLoading) return
        isLoading = true

        val params = buildProfileParams(type)
        ApiConfig.RequestToVolley({ result, response ->
            handleProfileResponse(result, response)
        }, activity, Constant.TRIP_LIST, params, true, 1)
    }

    private fun buildProfileParams(type: String): HashMap<String, String> {
        return hashMapOf(
            Constant.USER_ID to session.getData(Constant.USER_ID),
            Constant.TYPE to type,
            Constant.OFFSET to offset.toString(),
            Constant.LIMIT to limit.toString(),
            "date" to (selectedDate ?: "")
        )
    }

    private fun handleProfileResponse(result: Boolean, response: String) {
        isLoading = false
        binding.swipeRefreshLayout.isRefreshing = false

        if (result) {
            try {
                val jsonObject = JSONObject(response)
                if (jsonObject.getBoolean(Constant.SUCCESS)) {
                    total = jsonObject.getString(Constant.TOTAL).toInt()
                    session.setData(Constant.TOTAL, total.toString())
                    updateProfileList(jsonObject)
                } else {
                    showProfileListError(jsonObject.getString(Constant.MESSAGE))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateProfileList(jsonObject: JSONObject) {
        binding.rvProfileList.visibility = View.VISIBLE

        if (offset == 0) {
            homeProfileList.clear()
        }

        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
        val gson = Gson()

        for (i in 0 until jsonArray.length()) {
            val profile = gson.fromJson(jsonArray.getJSONObject(i).toString(), HomeProfile::class.java)
            homeProfileList.add(profile)
        }

        if (offset == 0) {
            homeProfilesAdapter.notifyDataSetChanged()
        } else {
            homeProfilesAdapter.notifyItemRangeInserted(offset, homeProfileList.size - 1)
        }

        offset += limit
    }

    private fun showProfileListError(message: String) {
        binding.rvProfileList.visibility = View.GONE
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    inner class HomeCategorysAdapter(
        private val activity: Activity,
        private val homeCategoryList: ArrayList<HomeCategory>
    ) : RecyclerView.Adapter<HomeCategorysAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_home_category, parent, false)
            return ItemHolder(v)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val category = homeCategoryList[position]

            holder.tvName.text = if (position == 2 && selectedDate != null) {
                selectedDate
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
                binding.rvProfileList.scrollToPosition(0)
                offset = 0
                when (position) {
                    0 -> {
                        currentType = "nearby"
                        loadProfileList(currentType)
                    }
                    1 -> {
                        currentType = "latest"
                        loadProfileList(currentType)
                    }
                    2 -> showDatePickerDialog(holder.tvName)

                    4 -> {

                    }
                    5 -> {

                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return homeCategoryList.size
        }

        inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvName: TextView = itemView.findViewById(R.id.tvName)
            val cardView: CardView = itemView.findViewById(R.id.cardView)
            val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
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
                this@HomeFragment.selectedDate = dateFormat.format(selectedDate.time)

                tvName.text = this@HomeFragment.selectedDate
                currentType = "date"
                loadProfileList(currentType)
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}

