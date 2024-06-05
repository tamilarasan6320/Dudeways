package com.app.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.databinding.ActivityCustomerSupportBinding

class CustomerSupportActivity : AppCompatActivity() {

    lateinit var binding: ActivityCustomerSupportBinding
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}
