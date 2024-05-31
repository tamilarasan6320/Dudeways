package com.app.dudeways.Activity.Trip.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.dudeways.Activity.Trip.StarttripActivity
import com.app.dudeways.databinding.FragmentThreeBinding
import com.app.dudeways.helper.Session


class threeFragment : Fragment() {

    lateinit var binding: FragmentThreeBinding
    lateinit var activity: Activity
    lateinit var session: Session


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentThreeBinding.inflate(layoutInflater)

        activity = requireActivity()
        session = Session(activity)

        (activity as StarttripActivity).binding.tvTitle.visibility = View.GONE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnBack.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Next"





        return binding.root
    }


}