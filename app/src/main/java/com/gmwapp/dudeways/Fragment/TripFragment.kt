package com.gmwapp.dudeways.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gmwapp.dudeways.Activity.HomeActivity
import com.gmwapp.dudeways.Activity.MytripsActivity
import com.gmwapp.dudeways.Activity.Trip.StarttripActivity
import com.gmwapp.dudeways.databinding.FragmentTripBinding


class TripFragment : Fragment() {

    lateinit var binding: FragmentTripBinding
    lateinit var activity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentTripBinding.inflate(inflater, container, false)

        activity = requireActivity()

        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE
        (activity as HomeActivity).binding.ivSearch.visibility = View.GONE


        binding.btnStart.setOnClickListener {
            val intent = Intent(activity, StarttripActivity::class.java)
            startActivity(intent)
        }

        binding.btnMytrip.setOnClickListener {
            val intent = Intent(activity, MytripsActivity::class.java)
            startActivity(intent)

        }




        return binding.root


    }
}