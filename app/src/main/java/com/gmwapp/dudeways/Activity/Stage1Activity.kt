package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityStage1Binding
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
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

        val proof1 = session.getData(Constant.SELFIE_IMAGE)
        val proof2 = session.getData(Constant.FRONT_IMAGE)
        val proof3 = session.getData(Constant.BACK_IMAGE)

        // if proof1 is not null then show the image

        if (proof1.isEmpty()) {
            binding.ivProofCheck1.visibility = View.GONE

        }
        else{
            binding.ivProofCheck1.visibility = View.VISIBLE
        }

        if (proof2.isEmpty() || proof3.isEmpty()){
            binding.ivProofCheck2.visibility = View.GONE
        }
        else{
            binding.ivProofCheck2.visibility = View.VISIBLE
        }



        binding.llStep1.setOnClickListener {

            if (proof1.isEmpty()) {
                val intent = Intent(activity, Stage2Activity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }
            else{

            }

        }

        binding.llStep2.setOnClickListener {




            if(proof2.isEmpty() || proof3.isEmpty()){
                val intent = Intent(activity, Stage3Activity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }
            else{

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