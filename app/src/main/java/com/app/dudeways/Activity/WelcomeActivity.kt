package com.app.dudeways.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.app.dudeways.R
import com.app.dudeways.WelcomePagerAdapter

class WelcomeActivity : AppCompatActivity() {

    private lateinit var mSlideViewPager: ViewPager
    private lateinit var mDotLayout: LinearLayout
    private lateinit var skipBtn: TextView

    private lateinit var dots: Array<TextView?>
    private lateinit var viewPagerAdapter: WelcomePagerAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        skipBtn = findViewById(R.id.tvSkip)
        skipBtn.setOnClickListener {
            navigateToLogin()
        }

        mSlideViewPager = findViewById(R.id.slideViewPager)
        mDotLayout = findViewById(R.id.indicator_layout)

        viewPagerAdapter = WelcomePagerAdapter(this)
        mSlideViewPager.adapter = viewPagerAdapter

        setUpIndicator(0)
        mSlideViewPager.addOnPageChangeListener(viewListener)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpIndicator(position: Int) {
        dots = arrayOfNulls(3)
        mDotLayout.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226")
            dots[i]?.textSize = 55f
            dots[i]?.setTextColor(resources.getColor(R.color.inactive, theme))
            mDotLayout.addView(dots[i])
        }
        dots[position]?.setTextColor(resources.getColor(R.color.active, theme))
    }

    private val viewListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onPageSelected(position: Int) {
            setUpIndicator(position)
            if (position == dots.size - 1) {
                navigateToLogin()
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun navigateToLogin() {
        val intent = Intent(this, MobileLoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}