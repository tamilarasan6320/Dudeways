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
import gmw.app.dudeways.Adapter.ChatlistAdapter
import gmw.app.dudeways.Model.Chatlist
import gmw.app.dudeways.databinding.FragmentMessagesBinding


class MessagesFragment : Fragment() {

    lateinit var binding: FragmentMessagesBinding
    lateinit var activity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessagesBinding.inflate(inflater, container, false)


        activity = requireActivity()


        binding.civProfile.setOnClickListener {
            val intent = Intent(activity, ProfileViewActivity::class.java)
            startActivity(intent)
        }

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvChat.layoutManager = linearLayoutManager


        NotificationList()

        return binding.root

    }


    private fun NotificationList() {
        val connect = ArrayList<Chatlist>()
        val cat1 = Chatlist("1", "Shafeeka", "")




        repeat(5){
            connect.add(cat1)
        }




        val chatlistAdapter = ChatlistAdapter(requireActivity(),connect)
        binding.rvChat.adapter = chatlistAdapter
    }



}