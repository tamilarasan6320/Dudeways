package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import com.gmwapp.dudeways.databinding.ActivitySpinwheelBinding
import java.util.Random

class SpinwheelActivity : BaseActivity() {

    lateinit var binding: ActivitySpinwheelBinding
    lateinit var activity: Activity

    val sectors = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    val sectorDegrees = IntArray(sectors.size)
    val sectorDegree = 360 / sectors.size

    var random: Random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpinwheelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this

        binding.spinButton.setOnClickListener {
            spin()
        }
    }

    private fun getDegreeForSectors(): IntArray {
        for (i in sectors.indices) {
            sectorDegrees[i] = (i + 1) * sectorDegree
        }
        return sectorDegrees
    }

    private fun spin() {
        val degree = random.nextInt(sectors.size)
        val toDegree = (360 * sectors.size) + sectorDegrees[degree].toFloat()

        val rotateAnimation = RotateAnimation(
            0f, toDegree,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 3600
        rotateAnimation.fillAfter = true
        rotateAnimation.interpolator = DecelerateInterpolator()
        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                // Toast the selected sector
                Toast.makeText(activity, "Selected sector: ${sectors[degree]}", Toast.LENGTH_SHORT).show()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        binding.wheelImage.startAnimation(rotateAnimation)
    }
}
