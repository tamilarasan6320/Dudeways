package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityFreePointsBinding
import com.app.dudeways.databinding.ActivityPurchasepointBinding
import com.app.dudeways.helper.Session

class FreePointsActivity : AppCompatActivity() {

    lateinit var binding: ActivityFreePointsBinding
    lateinit var activity: Activity
    lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_points)

        binding = ActivityFreePointsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)


        binding.llStep1.setOnClickListener {

            startActivity(Intent(activity, IdverficationActivity::class.java))

        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.llStep2.setOnClickListener {

            startActivity(Intent(activity, spinActivity::class.java))

        }


    }
}