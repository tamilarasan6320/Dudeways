package com.app.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivitySpinwheelBinding
import com.app.dudeways.helper.Session
import java.util.Random

class SpinwheelActivity : AppCompatActivity() {

    lateinit var binding: ActivitySpinwheelBinding
    lateinit var activity: Activity
    lateinit var session: Session

    var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinwheel)

        binding = ActivitySpinwheelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)



    }
}
