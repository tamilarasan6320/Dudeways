package com.app.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityNotificationBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject

class NotificationActivity : AppCompatActivity() {

    lateinit var binding: ActivityNotificationBinding
    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        activity = this
        session = Session(activity)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        if (session.getData(Constant.MESSAGE_NOTIFY).equals("1", ignoreCase = true)) {
            binding.cb1.isChecked = true
        }
        if (session.getData(Constant.ADD_FRIEND_NOTIFY).equals("1", ignoreCase = true)) {
            binding.cb2.isChecked = true
        }
        if (session.getData(Constant.VIEW_NOTIFY).equals("1", ignoreCase = true)) {
            binding.cb3.isChecked = true
        }




        binding.btnUpdate.setOnClickListener {
            val messageNotify = if (binding.cb1.isChecked) "1" else "0"
            val addFriendNotify = if (binding.cb2.isChecked) "1" else "0"
            val viewNotify = if (binding.cb3.isChecked) "1" else "0"
            updateNotification(messageNotify, addFriendNotify, viewNotify)
        }
    }

    private fun updateNotification(messageNotify: String, addFriendNotify: String, viewNotify: String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.MESSAGE_NOTIFY] = messageNotify
        params[Constant.ADD_FRIEND_NOTIFY] = addFriendNotify
        params[Constant.VIEW_NOTIFY] = viewNotify

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    session.setData(Constant.MESSAGE_NOTIFY, messageNotify)
                    session.setData(Constant.ADD_FRIEND_NOTIFY, addFriendNotify)
                    session.setData(Constant.VIEW_NOTIFY, viewNotify)
                    Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.UPDATE_NOTIFY, params, true, 1)
    }
}
