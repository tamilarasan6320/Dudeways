package com.app.dudeways.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.dudeways.Activity.HomeActivity
import com.app.dudeways.Activity.ProfileViewActivity
import com.app.dudeways.Adapter.ChatlistAdapter
import com.app.dudeways.Model.Chatlist
import com.app.dudeways.databinding.FragmentMessagesBinding


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

        (activity as HomeActivity).binding.rltoolbar.visibility = View.VISIBLE


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