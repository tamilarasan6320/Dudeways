package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityNotificationBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject

class NotificationActivity : BaseActivity() {

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
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {


                        userdetails(session.getData(Constant.USER_ID))
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()


                    } else {

                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            // Stop the refreshing animation once the network request is complete

        }, activity, Constant.UPDATE_NOTIFY, params, true, 1)
    }


    private fun userdetails(user_id: String?) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = user_id.toString()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)


                        session.setData(Constant.MESSAGE_NOTIFY, jsonobj.getString(Constant.MESSAGE_NOTIFY))
                        session.setData(Constant.ADD_FRIEND_NOTIFY, jsonobj.getString(Constant.ADD_FRIEND_NOTIFY))
                        session.setData(Constant.VIEW_NOTIFY, jsonobj.getString(Constant.VIEW_NOTIFY))



                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.USERDETAILS, params, true, 1)
    }

}
