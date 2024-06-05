package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityHomeBinding
import com.app.dudeways.databinding.ActivityStage4Binding
import com.app.dudeways.helper.Session

class Stage4Activity : AppCompatActivity() {

    lateinit var binding: ActivityStage4Binding
    lateinit var activity: Activity
    lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stage4)

        binding = ActivityStage4Binding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        binding.btnVerify.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}