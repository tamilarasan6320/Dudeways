package com.gmwapp.dudeways.Activity.Trip.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmwapp.dudeways.Activity.Trip.StarttripActivity
import com.gmwapp.dudeways.databinding.FragmentThreeBinding
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session


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

        (activity as StarttripActivity).binding.ivBack.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Next"


        if (session.getData(Constant.TRIP_LOCATION) != null) {
            binding.etLocation.setText(session.getData(Constant.TRIP_LOCATION))
        }



        return binding.root
    }


}