package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivitySplashscreenBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.google.android.gms.auth.api.signin.GoogleSignIn
import org.json.JSONException
import org.json.JSONObject

class SplashscreenActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashscreenBinding
    private var handler: Handler? = null
    private var activity: Activity? = null
   lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splashscreen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        activity = this
        session = Session(activity)
        handler = Handler()
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)

        setContentView(binding.root)

        GotoActivity()
    }


    private fun GotoActivity() {
        handler?.postDelayed({
            if (session!!.getBoolean("is_logged_in")) {
                login()

            } else {
             //   Toast.makeText(activity, "Please login", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 100)
    }


    override fun onStart() {
        super.onStart()

    }

    private fun login() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.EMAIL] = session.getData(Constant.EMAIL)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        val registered = jsonObject.getString("registered")
                        if (registered == "true") {
                            val `object` = JSONObject(response)
                            val jsonobj = `object`.getJSONObject(Constant.DATA)
                            session.setData(Constant.USER_ID, jsonobj.getString(Constant.ID))
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
                            session.setData(Constant.COVER_IMG, jsonobj.getString(Constant.COVER_IMG))

                            if (GoogleSignIn.getLastSignedInAccount(this) != null) {
                                val intent = Intent(activity,HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                        }

                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.CHECK_EMAIL, params, true, 1)
    }
}