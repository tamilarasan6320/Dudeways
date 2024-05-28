package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.databinding.ActivityMobileLoginBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject

class MobileLoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityMobileLoginBinding
    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMobileLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        binding.btnGenerateOtp.setOnClickListener {

            if (binding.etMobileNumber.text.toString().isEmpty()) {
                binding.etMobileNumber.error = "Please enter mobile number"
                return@setOnClickListener
            }
            else if (binding.etMobileNumber.text.toString().length != 10) {
                binding.etMobileNumber.error = "Please enter valid mobile number"
                return@setOnClickListener
            }
            else{
                session.setData(Constant.MOBILE,binding.etMobileNumber.text.toString())
                login()
            }



        }



    }



    private fun login() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.MOBILE] = session.getData(Constant.MOBILE)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {



                        val registered = jsonObject.getString("registered")


                        if (registered == "true") {
                            val intent = Intent(activity,OtpActivity::class.java)
                            session.setData("login", "1")
                            startActivity(intent)
                            finish()
                        } else {
                             val intent = Intent(activity,OtpActivity::class.java)
                            session.setData("login", "0")
                            startActivity(intent)
                            finish()

                        }





                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.CHECK_MOBILE, params, true, 1)
    }

}