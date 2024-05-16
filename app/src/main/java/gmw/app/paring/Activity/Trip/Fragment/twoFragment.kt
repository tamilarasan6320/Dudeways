package gmw.app.paring.Activity.Trip.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gmw.app.paring.Activity.Trip.StarttripActivity
import gmw.app.paring.R
import gmw.app.paring.databinding.FragmentTwoBinding


class twoFragment : Fragment() {

   lateinit var binding: FragmentTwoBinding
   lateinit var activity: Activity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTwoBinding.inflate(layoutInflater)

        activity = requireActivity()

        (activity as StarttripActivity).binding.tvTitle.visibility = View.GONE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnBack.visibility = View.VISIBLE

        return binding.root


    }


}