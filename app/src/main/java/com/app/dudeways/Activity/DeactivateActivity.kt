package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityDeactivateBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject

class DeactivateActivity : BaseActivity() {


    lateinit var binding: ActivityDeactivateBinding
    lateinit var activity: Activity
    lateinit var session: Session


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deactivate)

        binding = ActivityDeactivateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnfeedback.setOnClickListener {

            if (binding.etFeedback.text.toString().isEmpty()) {
                binding.etFeedback.error = "Please enter feedback"
                return@setOnClickListener
            }else{
                apicall()
            }

        }


    }

    private fun apicall() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params["feedback"] = binding.etFeedback.text.toString()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            // Stop the refreshing animation once the network request is complete

        }, activity, Constant.ADD_FEEDBACK, params, true, 1)

    }
}