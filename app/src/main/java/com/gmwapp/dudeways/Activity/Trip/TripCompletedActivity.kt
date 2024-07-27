package com.gmwapp.dudeways.Activity.Trip

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.gmwapp.dudeways.Activity.BaseActivity
import com.gmwapp.dudeways.Activity.MytripsActivity
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityTripCompletedBinding
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session

class TripCompletedActivity : BaseActivity() {


    lateinit var binding: ActivityTripCompletedBinding
    lateinit var activity: Activity
    lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_completed)

        binding = ActivityTripCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        session.setData(Constant.TRIP_LOCATION, "")
        session.setData(Constant.TRIP_TITLE, "")
        session.setData(Constant.TRIP_DESCRIPTION, "")
        session.setData(Constant.TRIP_FROM_DATE,"")
        session.setData(Constant.TRIP_TO_DATE,"")

        binding.btnMytrip.setOnClickListener {
            val intent = Intent(activity, MytripsActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
        // Handler to move to the next activity after 3 seconds

}
