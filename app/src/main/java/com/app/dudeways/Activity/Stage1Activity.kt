package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityStage1Binding
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.bumptech.glide.Glide

class Stage1Activity : BaseActivity() {

    lateinit var activity: Activity
    lateinit var binding: ActivityStage1Binding
    lateinit var session: Session
    // boolean llStep1 is clicked
    var isStep1Clicked = false
    // boolean llStep2 is clicked
    var isStep2Clicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stage1)

        binding = ActivityStage1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        binding.ivBack.setOnClickListener {
           onBackPressed()
        }

        Glide.with(activity).load(session.getData(Constant.PROFILE)).placeholder(R.drawable.profile_placeholder).into(binding.civProfile)

        val proof1 = session.getData(Constant.PROOF1)
        val proof2 = session.getData(Constant.PROOF2)

        if (proof1 == "1") {
            isStep1Clicked = true
            binding.ivProofCheck1.visibility = View.VISIBLE

        }

        if (proof2 == "1") {
            isStep2Clicked = true
            binding.ivProofCheck2.visibility = View.VISIBLE
        }

        binding.llStep1.setOnClickListener {
            if (isStep1Clicked) {

            } else {
                val intent = Intent(activity, Stage2Activity::class.java)
                startActivity(intent)
                finish()
            }

        }

        binding.llStep2.setOnClickListener {
            if (isStep2Clicked) {

            } else {
                val intent = Intent(activity, Stage3Activity::class.java)
                startActivity(intent)
                finish()
            }


        }


        binding.btnRequestVerification.setOnClickListener {
            if (isStep1Clicked && isStep2Clicked) {
                // Proceed to the next stage
                startActivity(Intent(this, Stage4Activity::class.java))
                finish()
            } else {
                Toast.makeText(activity, "Please complete all the steps", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // onbackpressed
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(activity, IdverficationActivity::class.java)
        startActivity(intent)
        finish()

        // close the app
    }

}