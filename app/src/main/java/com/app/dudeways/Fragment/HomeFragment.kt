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
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var activity: Activity
    lateinit var session: Session
    private var selectedItemPosition = 0 // Set default position to 0
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

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvProfileList.layoutManager = linearLayoutManager

        binding.rvCategoryList.layoutManager = GridLayoutManager(activity, 3)

        binding.swipeRefreshLayout.setOnRefreshListener {

            if (selectedItemPosition == 0) {
                ProfileList("nearby")
            } else if (selectedItemPosition == 1) {
                ProfileList("latest")
            } else if (selectedItemPosition == 2) {
                ProfileList("date")
            }


        }

        ProfileList("nearby")
        categoryList()

        // Update UI to reflect default selection
        binding.rvCategoryList.post {
            binding.rvCategoryList.findViewHolderForAdapterPosition(selectedItemPosition)?.itemView?.let { view ->
                val cardView: CardView = view.findViewById(R.id.cardView)
                val tvName: TextView = view.findViewById(R.id.tvName)
                cardView.setCardBackgroundColor(activity.resources.getColor(R.color.primary))
                tvName.setTextColor(activity.resources.getColor(R.color.white))
                tvName.setTypeface(null, Typeface.BOLD)
            }
        }

        return binding.root
    }

    fun ProfileList(type: String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.TYPE] = type
        params["date"] = selectedDated.toString()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val g = Gson()
                        val homeProfile = ArrayList<HomeProfile>()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            if (jsonObject1 != null) {
                                binding.rvProfileList.visibility = View.VISIBLE
                                val profile = g.fromJson(jsonObject1.toString(), HomeProfile::class.java)
                                homeProfile.add(profile)
                            }
                        }
                        val homePtofilesAdapter = HomePtofilesAdapter(requireActivity(), homeProfile)
                        binding.rvProfileList.adapter = homePtofilesAdapter
                    } else {
                        binding.rvProfileList.visibility = View.GONE
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            // Stop the refreshing animation once the network request is complete
            binding.swipeRefreshLayout.isRefreshing = false
        }, activity, Constant.TRIP_LIST, params, true, 1)
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

    inner class HomeCategorysAdapter(private val activity: Activity, private val homeCategory: ArrayList<HomeCategory>) : RecyclerView.Adapter<HomeCategorysAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_home_category, parent, false)
            return ItemHolder(v)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val itemHolder = holder
            val category = homeCategory[position]

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
            return homeCategory.size
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

                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
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
