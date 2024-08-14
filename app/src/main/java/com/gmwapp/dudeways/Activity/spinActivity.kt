package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivitySpinBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject
import java.util.Random

class spinActivity : BaseActivity() {
    private var degree = 0
    private var isSpinning = false

    private var wheel1: ImageView? = null

    private lateinit var binding: ActivitySpinBinding
    private lateinit var activity: Activity
    private lateinit var session: Session

    private val sectors = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    private val sectorDegrees = IntArray(sectors.size)
    private val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        val spinBtn = findViewById<Button>(R.id.spinBtn)
        wheel1 = findViewById(R.id.wheel1)

        degreeForSectors()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        spinBtn.setOnClickListener {
            if (!isSpinning) {
                spin()
                isSpinning = true
            }
        }
    }

    private fun spin() {
        // Set the degree to stop at sector 6
        val degreeToStop = 10 // Index 5 corresponds to sector 6

        // Calculate the final rotation degree based on the selected sector
        val rotateDegrees = 360 * 5 + sectorDegrees[degreeToStop]

        val rotateAnimation = RotateAnimation(
            0f, rotateDegrees.toFloat(),
            RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )

        rotateAnimation.duration = 3600
        rotateAnimation.fillAfter = true
        rotateAnimation.interpolator = DecelerateInterpolator()
        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                isSpinning = true
            }

            override fun onAnimationEnd(animation: Animation) {
                isSpinning = false

                // Process the sector based on its value (sector 6 in this case)
                val sectorValue = sectors[degreeToStop]
                addPurchase(sectorValue)
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })

        wheel1?.startAnimation(rotateAnimation)
    }

    private fun degreeForSectors() {
        val sectorDegree = 360 / sectors.size
        for (i in sectors.indices) {
            sectorDegrees[i] = (i + 1) * sectorDegree
        }
    }

    private fun addPurchase(point: String) {
        var points = ""
        when (point) {
//            "1" -> points = "20"
//            "2" -> points = "30"
//            "3" -> points = "40"
//            "4" -> points = "50"
//            "5" -> points = "100"
//            "6" -> points = "10"
//
            "1" -> points = "10"
            "2" -> points = "20"
            "3" -> points = "30"
            "4" -> points = "40"
            "5" -> points = "50"
            "6" -> points = "100"

        }
      //  Toast.makeText(activity, "You have won $points points", Toast.LENGTH_SHORT).show()

        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params["points"] ="10"

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val dataObj = jsonObject.getJSONObject(Constant.DATA)
                        session.setData(Constant.POINTS, dataObj.getString(Constant.POINTS))
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.SPIN_POINTS, params, true, 1)
    }
}
