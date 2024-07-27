package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityStorepointsBinding

class StorepointsActivity : BaseActivity() {


    lateinit var binding: ActivityStorepointsBinding
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storepoints)

        binding = ActivityStorepointsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}