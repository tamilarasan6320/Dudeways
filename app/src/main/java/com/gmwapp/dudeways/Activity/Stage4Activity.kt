package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityStage4Binding
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.bumptech.glide.Glide

class Stage4Activity : BaseActivity() {

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
           onBackPressed()
        }

        binding.ivBack.setOnClickListener{
            onBackPressed()
        }

        Glide.with(activity).load(session.getData(Constant.PROFILE)).placeholder(R.drawable.profile_placeholder).into(binding.civProfile)



    }
}