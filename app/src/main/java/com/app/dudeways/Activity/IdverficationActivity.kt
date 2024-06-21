package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityIdverficationBinding

class IdverficationActivity : AppCompatActivity() {

    lateinit var binding: ActivityIdverficationBinding
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idverfication)

        binding = ActivityIdverficationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        binding.btnStart.setOnClickListener {
            val intent = Intent(activity, Stage1Activity::class.java)
            startActivity(intent)
        }


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }
}