package com.app.dudeways.Activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.R

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme()
    }

    private fun setAppTheme() {
        if (isDarkModeEnabled()) {
            setTheme(R.style.Theme_Paring_Dark)
        } else {
            setTheme(R.style.Theme_Paring)
        }
    }

    private fun isDarkModeEnabled(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}
