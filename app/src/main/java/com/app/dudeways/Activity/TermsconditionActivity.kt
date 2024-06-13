package com.app.dudeways.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityTermsconditionBinding

class TermsconditionActivity : AppCompatActivity() {

    lateinit var binding: ActivityTermsconditionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termscondition)
        binding = ActivityTermsconditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {

            onBackPressed()

        }
    }
}