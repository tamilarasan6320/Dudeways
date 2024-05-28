package com.app.dudeways.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.app.dudeways.R
import com.app.dudeways.WelcomePagerAdapter

class WelcomeActivity : AppCompatActivity() {

    private var mSlideViewPager: ViewPager? = null
    private var mDotLayout: LinearLayout? = null

    private var nextBtn: Button? = null
    private var skipBtn: TextView? = null

    private lateinit var dots: Array<TextView?>
    private var viewPagerAdapter: WelcomePagerAdapter? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        nextBtn = findViewById(R.id.btnNext)
        skipBtn = findViewById(R.id.tvSkip)

//        backBtn!!.setOnClickListener {
//            if (getItem(0) > 0) {
//                mSlideViewPager!!.setCurrentItem(getItem(-1), true)
//            }
//        }

        nextBtn!!.setOnClickListener {
            if (getItem(0) < 2) {
                mSlideViewPager!!.setCurrentItem(getItem(1), true)
            } else {
                val intent = Intent(this, MobileLoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        skipBtn!!.setOnClickListener {
            val intent = Intent(this, MobileLoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        mSlideViewPager = findViewById(R.id.slideViewPager)
        mDotLayout = findViewById(R.id.indicator_layout)

        viewPagerAdapter = WelcomePagerAdapter(this)
        mSlideViewPager!!.adapter = viewPagerAdapter

        setUpIndicator(0)
        mSlideViewPager!!.addOnPageChangeListener(viewListener)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpIndicator(position: Int) {
        dots = arrayOfNulls(3) // Change array length to 3
        mDotLayout!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226")
            dots[i]?.textSize = 55f
           dots[i]?.setTextColor(resources.getColor(R.color.inactive, theme))
            mDotLayout!!.addView(dots[i])
        }
       dots[position]?.setTextColor(resources.getColor(R.color.active, theme))
    }

    private val viewListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onPageSelected(position: Int) {
            setUpIndicator(position)
           // backBtn?.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun getItem(i: Int): Int {
        return mSlideViewPager!!.currentItem + i
    }
}
