package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityGoogleLoginBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.zoho.commons.InitConfig
import com.zoho.livechat.android.listeners.InitListener
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONException
import org.json.JSONObject

class GoogleLoginActivity : BaseActivity() {

    lateinit var binding: ActivityGoogleLoginBinding
    lateinit var activity: Activity
    lateinit var session: Session
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_google_login)

        binding = ActivityGoogleLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        initializeZohoSalesIQ()

        FirebaseApp.initializeApp(this)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // firebaseAuth getInstance(...) must not be null
        firebaseAuth = FirebaseAuth.getInstance()

        binding.googlelogin.setOnClickListener { view: View? ->
            //  Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            signInGoogle()
        }

        binding.tvHelp.setOnClickListener {
            ZohoSalesIQ.Chat.show()
        }


        val login = session.getData("login")

        if (login == "1") {
            binding.tvmore.visibility = View.VISIBLE
        }
        else {
            binding.tvmore.visibility = View.GONE
        }

        binding.tvmore.setOnClickListener {
            showLoginDialog()
        }

    }

    private fun initializeZohoSalesIQ() {
        val initConfig = InitConfig()
        ZohoSalesIQ.init(application, "FkbMlSXKPaATeaZN35ZmjRdYaU29Wkx2QMkU75bCptU3ZA8TYZl2%2B%2BvFc55TAwVG_in", "xHGPBNAi6lC%2Fm7ngAEy%2FPSgch2eW42oPrw91hcyRHElKYqtGLQkB%2FuE%2F7QPdaD9BbNzYtKPn9U0kV316gEW6vUjMNMCKY5Jey7HFiemj%2BueB02iLgVZl6g%3D%3D", initConfig, object :
            InitListener {
            override fun onInitSuccess() {
                // Initialization successful
            }

            override fun onInitError(errorCode: Int, errorMessage: String) {
                // Handle initialization errors
            }
        })
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    // onActivityResult() function : this is where
    // we provide the task and data for the Google Account
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // this is where we update the UI after Google signin takes place
    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                session.setData(Constant.EMAIL, account.email.toString())
                session.setData("login", "true")

                login()

//                SavedPreference.setEmail(this, account.email.toString())
//                SavedPreference.setUsername(this, account.displayName.toString())
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                finish()
            }
        }
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
                            session.setData(Constant.MOBILE, jsonobj.getString(Constant.MOBILE))
                            session.setData(Constant.COVER_IMG, jsonobj.getString(Constant.COVER_IMG))
                            session.setData(Constant.POINTS, jsonobj.getString(Constant.POINTS))
                            session.setData(Constant.REFER_CODE, jsonobj.getString(Constant.REFER_CODE))
                            session.setData(Constant.INTRODUCTION, jsonobj.getString(Constant.INTRODUCTION))
                            session.setBoolean("is_logged_in", true)
                            val intent = Intent(activity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()


                        } else if (registered == "false") {
                            val intent = Intent(activity, ProfileDetailsActivity::class.java)
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
        }, activity, Constant.CHECK_EMAIL, params, true, 1)
    }


    override fun onBackPressed() {

    }


    private fun showLoginDialog() {
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_login, null)

        dialog.setContentView(dialogView)

        val emailEditText: EditText = dialogView.findViewById(R.id.etEmail)
        val passwordEditText: EditText = dialogView.findViewById(R.id.etPassword)
        val loginButton: Button = dialogView.findViewById(R.id.btnLogin)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check if the entered email and password match the predefined values
            if (email == "testdudeways@gmail.com" && password == "test@123") {
                // Move to HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                session.setData(Constant.USER_ID,"50")
                session.setBoolean("is_logged_in", true)
                startActivity(intent)
                dialog.dismiss() // Dismiss dialog after login
            } else {
                // Show an error message (e.g., Toast or Snackbar)
                emailEditText.error = "Invalid email or password"
                passwordEditText.error = "Invalid email or password"
            }
        }

        dialog.show()
    }


}

