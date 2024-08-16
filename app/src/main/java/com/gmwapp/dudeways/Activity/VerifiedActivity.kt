package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityStage4Binding
import com.gmwapp.dudeways.databinding.ActivityVerifiedBinding
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session

class VerifiedActivity : BaseActivity() {

    lateinit var binding: ActivityVerifiedBinding
    lateinit var activity: Activity
    lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_verified)

        binding = ActivityVerifiedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivBack.setOnClickListener{
            onBackPressed()
        }




    }
}