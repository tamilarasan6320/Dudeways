package com.app.dudeways.Activity

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivitySeetingBinding

class SeetingActivity : BaseActivity() {

    private lateinit var binding: ActivitySeetingBinding
    private lateinit var activity: Activity
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        binding.darkModeSwitch.isChecked = sharedPreferences.getBoolean("dark_mode", false)

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            recreate() // Recreate activity to apply the new theme
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}
