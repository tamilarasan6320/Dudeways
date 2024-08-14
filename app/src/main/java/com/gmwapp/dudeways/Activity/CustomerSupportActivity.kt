package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.gmwapp.dudeways.databinding.ActivityCustomerSupportBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONException
import org.json.JSONObject

class CustomerSupportActivity : BaseActivity() {

    lateinit var binding: ActivityCustomerSupportBinding
    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)
        setting()
        binding.btnchatsupport.setOnClickListener {
            ZohoSalesIQ.Chat.show()
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

//        binding.rlInstagramLinkShare.setOnClickListener {
//            val link = session.getData(Constant.INSTAGRAM_LINK)
//
//            val shareBody = "" + link
//            val sharingIntent = android.content.Intent(android.content.Intent.ACTION_SEND)
//            sharingIntent.type = "text/plain"
//            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Dudeways")
//            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
//            startActivity(android.content.Intent.createChooser(sharingIntent, "Share using"))
//
//        }
//
//
//        binding.rlTelegramLinkShare.setOnClickListener {
//            val link = session.getData(Constant.TELEGRAM_LINK)
//
//            val shareBody = "" + link
//            val sharingIntent = android.content.Intent(android.content.Intent.ACTION_SEND)
//            sharingIntent.type = "text/plain"
//            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Dudeways")
//            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
//            startActivity(android.content.Intent.createChooser(sharingIntent, "Share using"))
//
//        }

        binding.rlInstagramLinkJoin.setOnClickListener {
            val link = session.getData(Constant.INSTAGRAM_LINK)
            val i = android.content.Intent(android.content.Intent.ACTION_VIEW)
            i.data = android.net.Uri.parse(link)
            startActivity(i)
        }

        binding.rlTelegramLinkJoin.setOnClickListener {
            val link = session.getData(Constant.TELEGRAM_LINK)
            val i = android.content.Intent(android.content.Intent.ACTION_VIEW)
            i.data = android.net.Uri.parse(link)
            startActivity(i)
        }


    }

    private fun setting() {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {


                      val array = jsonObject.getJSONArray("data")

                        session.setData(Constant.INSTAGRAM_LINK, array.getJSONObject(0).getString(Constant.INSTAGRAM_LINK))
                        session.setData(Constant.TELEGRAM_LINK, array.getJSONObject(0).getString(Constant.TELEGRAM_LINK))







                    } else {
                        Toast.makeText(
                            activity,
                            jsonObject.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(activity, response, Toast.LENGTH_SHORT).show()
            }

        }, activity, Constant.SETTINGS_LIST, params, true, 1)
    }
}
