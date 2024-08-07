package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityIdverficationBinding

class IdverficationActivity : BaseActivity() {

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
            finish()
        }


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }
}