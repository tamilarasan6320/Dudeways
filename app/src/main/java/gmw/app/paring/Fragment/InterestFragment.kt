package gmw.app.paring.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import gmw.app.paring.Activity.ProfileActivity
import gmw.app.paring.Activity.ProfileDetailsActivity
import gmw.app.paring.Activity.ProfileViewActivity
import gmw.app.paring.Adapter.ConnectAdapter
import gmw.app.paring.Adapter.NotificationAdapter
import gmw.app.paring.Model.Connect
import gmw.app.paring.Model.Notification
import gmw.app.paring.R
import gmw.app.paring.databinding.FragmentInterestBinding
import gmw.app.paring.databinding.FragmentNotificationBinding


class InterestFragment : Fragment() {


    lateinit var binding : FragmentInterestBinding
    lateinit var activity : Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInterestBinding.inflate(inflater, container, false)
        activity = requireActivity()



        binding.civProfile.setOnClickListener {

            val intent = Intent(activity, ProfileViewActivity::class.java)
            startActivity(intent)
        }

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvConnectList.layoutManager = linearLayoutManager


        NotificationList()

        return binding.root

    }


    private fun NotificationList() {
        val connect = ArrayList<Connect>()
        val cat1 = Connect("1", "Shafeeka", "")




        repeat(5){
            connect.add(cat1)
        }




        val connectAdapter = ConnectAdapter(requireActivity(),connect)
        binding.rvConnectList.adapter = connectAdapter
    }



}