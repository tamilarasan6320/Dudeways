package com.app.dudeways.Adapter

import android.app.Activity
import android.app.Dialog
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.dudeways.Fragment.HomeFragment
import com.app.dudeways.Model.HomeCategory
import com.app.dudeways.R
import com.app.dudeways.helper.Session
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeCategoryAdapter(
    private val activity: Activity,
    private val homeCategories: ArrayList<HomeCategory>

) : RecyclerView.Adapter<HomeCategoryAdapter.ItemHolder>() {

    private var selectedPosition = -1
    private var selectedDate: String? = null
    private var formattedDate: String? = null
    // call session


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.layout_home_category, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val category = homeCategories[position]


        // If position is 2 and a date has been selected, set the date text
        holder.tvName.text = if (position == 2 && selectedDate != null) {
            selectedDate
        } else {
            category.name
        }

        // Update the background color based on selection state
        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(activity.resources.getColor(R.color.primary))
            holder.tvName.setTextColor(activity.resources.getColor(R.color.white))
            holder.tvName.setTypeface(null, Typeface.BOLD)
        } else {
            holder.cardView.setCardBackgroundColor(activity.resources.getColor(R.color.white))
            holder.tvName.setTextColor(activity.resources.getColor(R.color.black))
            holder.tvName.setTypeface(null, Typeface.NORMAL)
        }

        holder.cardView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(position)
            if (position == 0) {


            } else if (position == 1) {

            } else if (position == 2) {
                showDatePickerDialog(holder.tvName)
            }


        }
    }

    override fun getItemCount(): Int = homeCategories.size

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            this.selectedDate = formattedDate  // Store the selected date

            // call onBindViewHolder
            notifyDataSetChanged()

            dialog.dismiss()
        }

        dialog.show()
    }



}
