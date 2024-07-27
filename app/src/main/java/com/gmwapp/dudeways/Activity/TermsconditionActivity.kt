package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityTermsconditionBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class TermsconditionActivity : BaseActivity() {

    lateinit var binding: ActivityTermsconditionBinding
    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termscondition)
        binding = ActivityTermsconditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this

        binding.ivBack.setOnClickListener {

            onBackPressed()

        }

        binding.wvPrivacy.setBackgroundColor(resources.getColor(R.color.primary_light, null))
        fetchPrivacyPolicy()
    }
    private fun fetchPrivacyPolicy() {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley({ result, response ->
            Log.d("API Response", response) // Log the API response
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val dataArray: JSONArray = jsonObject.getJSONArray(Constant.DATA)

                        val dataObject = dataArray.getJSONObject(0)
                        val terms_conditions = dataObject.getString("terms_conditions")
                        Log.d("Privacy Policy", terms_conditions) // Log the privacy policy content
                        binding.wvPrivacy.loadDataWithBaseURL("", terms_conditions, "text/html", "UTF-8", "")

                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(activity, response, Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.TERMS_CONDITIONS, params, true, 1)
    }
}