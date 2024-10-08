package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityPrivacypolicyBinding
import com.gmwapp.dudeways.databinding.ActivityRefundBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RefundActivity : BaseActivity() {

    private lateinit var binding: ActivityRefundBinding
    private lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRefundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        // Set the background color of the WebView
        binding.wvPrivacy.setBackgroundColor(resources.getColor(R.color.primary_light, null))

        fetchPrivacyPolicy()
        handleDeepLink(intent)

    }

    private fun handleDeepLink(intent: Intent?) {
        val action = intent?.action
        val data: Uri? = intent?.data

        if (Intent.ACTION_VIEW == action && data != null) {
            // Extract the user ID and chat ID from the query parameters
            val userId = data.getQueryParameter("userid")
            val chatId = data.getQueryParameter("chatid")

            // Display the extracted user ID and chat ID in a toast message
            if (userId != null && chatId != null) {
                Toast.makeText(this, "User ID: $userId, Chat ID: $chatId", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Missing user ID or chat ID", Toast.LENGTH_SHORT).show()
            }
        }
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
                        val privacyPolicy = dataObject.getString("refund_policy")
                        Log.d("Privacy Policy", privacyPolicy) // Log the privacy policy content

                        binding.wvPrivacy.loadDataWithBaseURL("", privacyPolicy, "text/html", "UTF-8", "")
                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(activity, response, Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.REFUND_POLICY, params, true, 1)
    }

}