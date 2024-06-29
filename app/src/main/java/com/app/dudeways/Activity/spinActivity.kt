package com.app.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivitySpinBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin)
        binding = ActivitySpinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        val spinBtn = findViewById<Button>(R.id.spinBtn)
        wheel1 = findViewById(R.id.wheel1)

        degreeForSectors

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
        degree = random.nextInt(sectors.size - 1)

        val rotateAnimation = RotateAnimation(
            0f, (360 * sectors.size + sectorDegrees[degree]).toFloat(),
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
             //   Toast.makeText(this@spinActivity, "You've got" + sectors[sectors.size - (degree + 1)], Toast.LENGTH_SHORT).show()
                isSpinning = false

                if (sectors[sectors.size - (degree + 1)] == "1") {
                    addpurchase("1")
                } else if (sectors[sectors.size - (degree + 1)] == "2") {
                    addpurchase("2")
                } else if (sectors[sectors.size - (degree + 1)] == "3") {
                    addpurchase("3")
                } else if (sectors[sectors.size - (degree + 1)] == "4") {
                    addpurchase("4")
                } else if (sectors[sectors.size - (degree + 1)] == "5") {
                    addpurchase("5")
                } else if (sectors[sectors.size - (degree + 1)] == "6") {
                    addpurchase("6")
                }

            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })

        wheel1!!.startAnimation(rotateAnimation)
    }

    private val degreeForSectors: Unit
        get() {
            val sectorDegree = 360 / sectors.size
            for (i in sectors.indices) {
                sectorDegrees[i] = (i + 1) * sectorDegree
            }
        }


    companion object {
        private val sectors = arrayOf("1", "2", "3", "4", "5", "6")
        private val sectorDegrees = IntArray(sectors.size)
        private val random = Random()
    }


    private fun addpurchase(point: String) {

        var points = ""
        if (point == "1") {
            points = "20"
        } else if (point == "2") {
            points = "30"
        } else if (point == "3") {
            points = "40"
        } else if (point == "4") {
            points = "50"
        } else if (point == "5") {
            points = "100"
        } else if (point == "6") {
            points = "10"
        }

        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
      //  params["points_id"] = id
        params["points"] = points
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)


                        session.setData(Constant.POINTS, jsonobj.getString(Constant.POINTS))
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