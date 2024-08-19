package com.gmwapp.dudeways.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.gmwapp.dudeways.Activity.HomeActivity
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.FragmentSearchBinding
import com.gmwapp.dudeways.helper.Session

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var activity: Activity
    private lateinit var session: Session

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        activity = requireActivity()
        session = Session(activity)

        // Hide toolbar and bottom navigation view
        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE
        (activity as HomeActivity).binding.bottomNavigationView.visibility = View.GONE

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        handleOnBackPressed()
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Replace the current fragment with HomeFragment
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            }
        })
    }
}
