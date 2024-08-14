package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivitySplashscreenBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SplashscreenActivity : BaseActivity() {
    lateinit var binding: ActivitySplashscreenBinding
    private var handler: Handler? = null
    private var activity: Activity? = null
   lateinit var session: Session

    private var currentVersion: String? = null

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

//        val videoView = findViewById<VideoView>(R.id.videoView)
//        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.logo_animation)
//
//        videoView.setVideoURI(videoUri)
//        videoView.start()

//        videoView.setOnCompletionListener {
//            GotoActivity()
//            // Do something when the video ends
//        }

        handleIncomingIntent(intent)

        setupViews()

    }

    override fun onResume() {
        setupViews()
        super.onResume()
    }

    private fun setupViews() {
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            currentVersion = pInfo.versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        appupdate()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        val data: Uri? = intent.data
        data?.let {
            if (it.isHierarchical) {
                val referralCode = it.getQueryParameter("referralCode")
                referralCode?.let {
                    Log.d("Referral Code", "referralCode = $referralCode")
//                    Toast.makeText(this, "Referral Code: $referralCode", Toast.LENGTH_LONG).show()
                    // Handle the referral code (e.g., store it in shared preferences, use it to fetch referral-specific data, etc.)
                }
            }
        }
    }

    private fun appupdate() {
        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.logo_animation)

        videoView.setVideoURI(videoUri)
        videoView.start()

        val params: MutableMap<String, String> = HashMap()
        activity?.let {
            ApiConfig.RequestToVolley({ result, response ->
                if (result) {
                    try {
                        val jsonObject = JSONObject(response)
                        if (jsonObject.getBoolean(Constant.SUCCESS)) {
                            val jsonArray: JSONArray = jsonObject.getJSONArray(Constant.DATA)


                            val latestVersion = jsonArray.getJSONObject(0).getString(Constant.APP_VERSION)
                            val link = jsonArray.getJSONObject(0).getString(Constant.LINK)
                            session.setData(Constant.LOGIN,jsonArray.getJSONObject(0).getString(Constant.LOGIN))

                            //   Toast.makeText(activity,latestVersion + currentVersion!!.toInt() , Toast.LENGTH_SHORT).show()
                            val description = jsonArray.getJSONObject(0).getString("description")
                            if (currentVersion!!.toInt() >= latestVersion.toInt()) {
                                videoView.setOnCompletionListener {
                                    GotoActivity()
                                    // Do something when the video ends
                                }
                            } else {
                                showUpdateDialog(link,description)
                            }

                        } else {
    //                        val jsonArray: JSONArray = jsonObject.getJSONArray(Constant.DATA)
    //
    //
    //                        val latestVersion = jsonArray.getJSONObject(0).getString(Constant.VERSION)
    //                        val link = jsonArray.getJSONObject(0).getString(Constant.LINK)
    //                        val description = jsonArray.getJSONObject(0).getString("description")
    //                        if (currentVersion!!.toInt() == latestVersion.toInt()) {
    //                            GotoActivity()
    //                        } else {
    //                            showUpdateDialog(link, description)
    //                        }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }, it, Constant.APPUPDATE, params, true)
        }
        // Return a dummy intent, as the actual navigation is handled inside the callback

        Log.d("AppUpdate", "API Endpoint: ${Constant.APPUPDATE}")
        Log.d("AppUpdate", "API Endpoint:: $params")
    }

    private fun showUpdateDialog(link: String, description: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_dialog_update, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.setCancelable(false);

        val btnUpdate = view.findViewById<View>(R.id.btnUpdate)
        val dialogMessage = view.findViewById<TextView>(R.id.dialog_message)
        dialogMessage.text = description
        btnUpdate.setOnClickListener(View.OnClickListener {
            val url = link;
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        })


        // Customize your bottom dialog here
        // For example, you can set text, buttons, etc.

        bottomSheetDialog.show()
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
        activity?.let {
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
                                session.setData(Constant.MOBILE, jsonobj.getString(Constant.MOBILE))
                                session.setData(Constant.REFER_CODE, jsonobj.getString(Constant.REFER_CODE))
                                session.setData(Constant.COVER_IMG, jsonobj.getString(Constant.COVER_IMG))
                                session.setData(Constant.POINTS, jsonobj.getString(Constant.POINTS))


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
            }, it, Constant.CHECK_EMAIL, params, true, 1)
        }
    }
}