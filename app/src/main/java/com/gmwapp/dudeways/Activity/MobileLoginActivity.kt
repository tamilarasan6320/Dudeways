package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.gmwapp.dudeways.databinding.ActivityMobileLoginBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject

class MobileLoginActivity : BaseActivity() {

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
                            val `object` = JSONObject(response)
                            val jsonobj = `object`.getJSONObject(Constant.DATA)
                            session.setData(Constant.NAME, jsonobj.getString(Constant.NAME))
                            session.setData(Constant.UNIQUE_NAME, jsonobj.getString(Constant.UNIQUE_NAME))
                            session.setData(Constant.EMAIL, jsonobj.getString(Constant.EMAIL))
                            session.setData(Constant.AGE, jsonobj.getString(Constant.AGE))
                            session.setData(Constant.GENDER, jsonobj.getString (Constant.GENDER))
                            session.setData(Constant.PROFESSION, jsonobj.getString(Constant.PROFESSION))
                            session.setData(Constant.STATE, jsonobj.getString(Constant.STATE))
                            session.setData(Constant.CITY, jsonobj.getString(Constant.CITY))
                            session.setData(Constant.PROFILE, jsonobj.getString(Constant.PROFILE))
                            session.setData(Constant.MOBILE, jsonobj.getString(Constant.MOBILE))
                            session.setData(Constant.REFER_CODE, jsonobj.getString(Constant.REFER_CODE))
                            val intent = Intent(activity,OtpActivity::class.java)
                            session.setData("login", "1")
                            startActivity(intent)
                            finish()
                        } else if (registered == "false") {
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