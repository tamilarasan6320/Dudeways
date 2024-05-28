package com.app.dudeways.Activity.Trip.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.dudeways.Activity.Trip.StarttripActivity
import com.app.dudeways.databinding.FragmentSixBinding


class SixFragment : Fragment() {


    lateinit var binding: FragmentSixBinding
    lateinit var activity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSixBinding.inflate(layoutInflater)
        activity = requireActivity()

        (activity as StarttripActivity).binding.tvTitle.visibility = View.GONE
        (activity as StarttripActivity).binding.btnNext.text = "Start Trip"
        (activity as StarttripActivity).binding.btnBack.visibility = View.VISIBLE



        return binding.root
    }


}