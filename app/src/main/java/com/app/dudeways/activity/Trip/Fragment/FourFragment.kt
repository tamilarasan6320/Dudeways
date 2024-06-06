package com.app.dudeways.activity.Trip.Fragment

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import com.app.dudeways.activity.Trip.StarttripActivity
import com.app.dudeways.R
import com.app.dudeways.databinding.FragmentFourBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FourFragment : Fragment() {


    lateinit var binding: FragmentFourBinding
    lateinit var activity: Activity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFourBinding.inflate(layoutInflater)

        activity = requireActivity()

        (activity as StarttripActivity).binding.ivBack.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.tvTitle.visibility = View.INVISIBLE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Next"



        binding.edEndDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.edStartDate.setOnClickListener {
            showDatePickerDialog1()
        }







        return binding.root
    }



    private fun showDatePickerDialog() {

        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_calendar)
        dialog.setCanceledOnTouchOutside(true)


        // Set dialog window to full screen
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val titleTextView = dialog.findViewById<TextView>(R.id.titleTextView)
        val calendarView = dialog.findViewById<CalendarView>(R.id.calendarView)

        // Set the minimum date to today
        calendarView.minDate = System.currentTimeMillis()

        // Set listener to handle date selection
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            // Format the selected date
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)

            // Set the formatted date to the EditText
            binding.edEndDate.setText(formattedDate)

            dialog.dismiss()
        }



        dialog.show()
    }
    private fun showDatePickerDialog1() {
        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_calendar)
        dialog.setCanceledOnTouchOutside(true)

        // Set dialog window to full screen
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val titleTextView = dialog.findViewById<TextView>(R.id.titleTextView)
        val calendarView = dialog.findViewById<CalendarView>(R.id.calendarView)

        // Set the minimum date to today
        calendarView.minDate = System.currentTimeMillis()

        // Set listener to handle date selection
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            // Format the selected date
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)

            // Set the formatted date to the EditText
            binding.edStartDate.setText(formattedDate)

            dialog.dismiss()
        }



        dialog.show()
    }



}