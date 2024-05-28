package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivitySplashscreenBinding
import com.app.dudeways.helper.Session

class SplashscreenActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashscreenBinding
    private var handler: Handler? = null
    private var activity: Activity? = null
   lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splashscreen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        activity = this
        session = Session(activity)
        handler = Handler()
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)

        setContentView(binding.root)

        GotoActivity()
    }


    private fun GotoActivity() {
        handler?.postDelayed({
            if (session!!.getBoolean("is_logged_in")) {
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(activity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 100)
    }
}