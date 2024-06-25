package com.app.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.databinding.ActivityInviteFriendsBinding
import com.app.dudeways.helper.Session

class InviteFriendsActivity : AppCompatActivity() {

    lateinit var binding: ActivityInviteFriendsBinding
    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInviteFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        binding.ivBack.setOnClickListener{
            onBackPressed()
        }

    }
}
