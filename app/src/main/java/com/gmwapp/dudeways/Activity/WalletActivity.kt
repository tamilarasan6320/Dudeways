package com.gmwapp.dudeways.Activity

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import com.gmwapp.dudeways.databinding.ActivityWalletBinding
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.helper.Session

class WalletActivity : BaseActivity() {

    private lateinit var binding: ActivityWalletBinding
    lateinit var activity: Activity
    lateinit var session: Session



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvAddBank.setOnClickListener {
            val intent = android.content.Intent(activity, BankDetailsActivity::class.java)
            startActivity(intent)
        }


    }


}