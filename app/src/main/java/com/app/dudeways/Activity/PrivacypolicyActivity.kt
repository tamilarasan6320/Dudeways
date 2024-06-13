package com.app.dudeways.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityPrivacypolicyBinding

class PrivacypolicyActivity : AppCompatActivity() {

    lateinit var binding: ActivityPrivacypolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacypolicy)
        binding = ActivityPrivacypolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {

            onBackPressed()

        }

    }
}