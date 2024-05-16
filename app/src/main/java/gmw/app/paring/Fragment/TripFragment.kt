package gmw.app.paring.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gmw.app.paring.Activity.Trip.StarttripActivity
import gmw.app.paring.R
import gmw.app.paring.databinding.FragmentTripBinding


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


        return binding.root


    }
}