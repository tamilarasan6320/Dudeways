package com.app.dudeways.Activity.Trip.Fragment

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.dudeways.Activity.Trip.StarttripActivity
import com.app.dudeways.databinding.FragmentFiveBinding
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session


class FiveFragment : Fragment() {


    lateinit var binding: FragmentFiveBinding
    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFiveBinding.inflate(layoutInflater)

        activity = requireActivity()
        session = Session(activity)

        (activity as StarttripActivity).binding.ivBack.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.tvTitle.visibility = View.INVISIBLE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Next"


        binding.etDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etDescription.lineCount > 5) {
                    val text = s.toString().substring(0, binding.etDescription.selectionEnd - 1)
                    binding.etDescription.setText(text)
                    binding.etDescription.setSelection(binding.etDescription.text!!.length)
                }
            }
        })

        if (session.getData(Constant.TRIP_TITLE) != null) {
            binding.etTripName.setText(session.getData(Constant.TRIP_TITLE))
        }

        if (session.getData(Constant.TRIP_DESCRIPTION) != null) {
            binding.etDescription.setText(session.getData(Constant.TRIP_DESCRIPTION))
        }

        return binding.root
    }


}