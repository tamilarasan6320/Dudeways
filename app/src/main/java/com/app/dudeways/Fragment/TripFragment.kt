package com.app.dudeways.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.dudeways.Activity.HomeActivity
import com.app.dudeways.Activity.MytripsActivity
import com.app.dudeways.Activity.Trip.StarttripActivity
import com.app.dudeways.databinding.FragmentTripBinding


class TripFragment : Fragment() {

    lateinit var binding: FragmentTripBinding
    lateinit var actvity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentTripBinding.inflate(inflater, container, false)


        binding.btnStart.setOnClickListener {
            val intent = Intent(activity, StarttripActivity::class.java)
            startActivity(intent)
        }

        binding.btnMytrip.setOnClickListener {
            val intent = Intent(activity, MytripsActivity::class.java)
            startActivity(intent)

        }


        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE

        return binding.root


    }
}