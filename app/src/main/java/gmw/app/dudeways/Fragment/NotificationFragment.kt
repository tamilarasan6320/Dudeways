package gmw.app.dudeways.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import gmw.app.dudeways.Activity.ProfileViewActivity
import gmw.app.dudeways.Adapter.NotificationAdapter
import gmw.app.dudeways.Model.Notification
import gmw.app.dudeways.databinding.FragmentNotificationBinding


class NotificationFragment : Fragment() {

    lateinit var binding: FragmentNotificationBinding
    lateinit var activity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        activity = requireActivity()


        binding.civProfile.setOnClickListener {

            val intent = Intent(activity, ProfileViewActivity::class.java)
            startActivity(intent)
        }


        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvNotificationList.layoutManager = linearLayoutManager


        NotificationList()

        return binding.root

    }


    private fun NotificationList() {
        val notification = ArrayList<Notification>()
        val cat1 = Notification("1", "Shafeeka", "")




        repeat(5){
            notification.add(cat1)
        }




        val notificationAdapter = NotificationAdapter(requireActivity(),notification)
        binding.rvNotificationList.adapter = notificationAdapter
    }



}