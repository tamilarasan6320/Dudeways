package com.gmwapp.dudeways.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.gmwapp.dudeways.Activity.GoogleLoginActivity
import com.gmwapp.dudeways.Activity.HomeActivity
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.FragmentMyProfileBinding
import com.gmwapp.dudeways.databinding.FragmentTripBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class MyProfileFragment : Fragment() {


    lateinit var binding: FragmentMyProfileBinding
    lateinit var activity: Activity
    lateinit var session: Session

    lateinit var mGoogleSignInClient: GoogleSignInClient

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    var filePath1: String? = null
    var imageUri: Uri? = null

    private val REQUEST_IMAGE_GALLERY = 2

    private val REQUEST_IMAGE_CAMERA = 3
    private var isCameraRequest = false

    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        activity = requireActivity()
        session = Session(activity)

        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)



        val verify = session.getData(Constant.VERIFIED)

        if (verify == "1"){
            binding.ivVerify.visibility = View.VISIBLE
        }
        else{
            binding.ivVerify.visibility = View.GONE
        }


        val profile = session.getData(Constant.PROFILE)
        Glide.with(activity).load(profile).placeholder(R.drawable.profile_placeholder)
            .into(binding.civProfile)
        Glide.with(activity).load(session.getData(Constant.COVER_IMG)).placeholder(R.drawable.placeholder_bg).into(binding.ivCover)

        binding.tvName.text = session.getData(Constant.NAME)
        binding.tvUsername.text = "@"+session.getData(Constant.UNIQUE_NAME)

        return binding.root

    }


    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                imageUri = data?.data
                if (isCameraRequest) {
                    // Configure CropImage for rectangular crop
                    CropImage.activity(imageUri)
                        .setAspectRatio(4, 2) // Set aspect ratio for a rectangle
                        .setCropShape(CropImageView.CropShape.RECTANGLE) // Set crop shape to rectangle
                        .start(activity)
                } else {
                    // Configure CropImage for circular crop
                    CropImage.activity(imageUri)
                        .setAspectRatio(1, 1) // Set aspect ratio to 1:1 for a square crop
                        .setCropShape(CropImageView.CropShape.OVAL) // Set crop shape to oval
                        .start(activity)
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(activity, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        if (isCameraRequest) {
                            binding.ivCover.setImageBitmap(myBitmap)
                            uploadCover()
                        } else {
                            binding.civProfile.setImageBitmap(myBitmap)
                            uploadProfile()
                        }
                    }
                }
            }
        }


    }


    private fun uploadProfile() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        val FileParams: MutableMap<String, String> = HashMap()
        FileParams[Constant.PROFILE] = filePath1!!
        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)

                        session.setData(Constant.NAME, jsonobj.getString(Constant.NAME))
                        session.setData(Constant.UNIQUE_NAME, jsonobj.getString(Constant.UNIQUE_NAME))
                        session.setData(Constant.EMAIL, jsonobj.getString(Constant.EMAIL))
                        session.setData(Constant.AGE, jsonobj.getString(Constant.AGE))
                        session.setData(Constant.GENDER, jsonobj.getString(Constant.GENDER))
                        session.setData(Constant.PROFESSION, jsonobj.getString(Constant.PROFESSION))
                        session.setData(Constant.STATE, jsonobj.getString(Constant.STATE))
                        session.setData(Constant.CITY, jsonobj.getString(Constant.CITY))
                        session.setData(Constant.PROFILE, jsonobj.getString(Constant.PROFILE))
                        session.setData(Constant.MOBILE, jsonobj.getString(Constant.MOBILE))
                        session.setData(Constant.REFER_CODE, jsonobj.getString(Constant.REFER_CODE))
                        session.setData(Constant.REFERRED_BY, jsonobj.getString(Constant.REFERRED_BY))
                        Glide.with(activity).load(session.getData(Constant.PROFILE)).placeholder(R.drawable.profile_placeholder).into(binding.civProfile)

                        // call resume() the HomeActivity
                        onResume()




                        Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.UPDATE_IMAGE, params, FileParams)
    }
    private fun uploadCover() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        val FileParams: MutableMap<String, String> = HashMap()
        FileParams[Constant.COVER_IMG] = filePath1!!
        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)

                        session.setData(Constant.COVER_IMG, jsonobj.getString(Constant.COVER_IMG))
                        Glide.with(activity).load(session.getData(Constant.COVER_IMG)).placeholder(R.drawable.placeholder_bg).into(binding.ivCover)

                        onResume()
                        Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.UPDATE_COVER_IMG, params, FileParams)
    }

    private fun showLogoutConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
            .setMessage("Are you sure you want to logout?")
            .setCancelable(true)
            .setPositiveButton("Logout") { dialog, _ ->
                // Perform logout action
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialogBuilder.show()

        // Change button text colors
        dialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.primary))
        dialogBuilder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, R.color.text_grey))
    }


    private fun logout() {
        googleSignInClient.signOut().addOnCompleteListener(activity) {
            // Clear session data and redirect to login
            clearSessionData(activity)
            redirectToLogin(activity)

        }
    }

    private fun clearSessionData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    private fun redirectToLogin(context: Context) {
        session.setBoolean("is_logged_in", false)
        val intent = Intent(context, GoogleLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        activity.finish()
    }

    private fun verification_list() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {

                        val dataArray = jsonObject.getJSONArray("data")
                        if (dataArray.length() > 0) {
                            val dataObject = dataArray.getJSONObject(0)
                            val selfieImageUrl = dataObject.getString("selfie_image")
                            val front_image = dataObject.getString("front_image")
                            val back_image = dataObject.getString("back_image")
                            val status = dataObject.getString("status")
                            val payment_status = dataObject.getString("payment_status")

                            session.setData(Constant.SELFIE_IMAGE, selfieImageUrl)
                            session.setData(Constant.FRONT_IMAGE, front_image)
                            session.setData(Constant.BACK_IMAGE, back_image)
                            session.setData(Constant.STATUS, status)
                            session.setData(Constant.PAYMENT_STATUS, payment_status)


                        } else {

                            session.setData(Constant.SELFIE_IMAGE, "")
                            session.setData(Constant.FRONT_IMAGE, "")
                            session.setData(Constant.BACK_IMAGE, "")
                            session.setData(Constant.STATUS, "")
                            session.setData(Constant.PAYMENT_STATUS, "")
                            //   Toast.makeText(activity, "No data available", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        session.setData(Constant.SELFIE_IMAGE, "")
                        session.setData(Constant.FRONT_IMAGE, "")
                        session.setData(Constant.BACK_IMAGE, "")
                        session.setData(Constant.STATUS, "")
                        session.setData(Constant.PAYMENT_STATUS, "")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.VERIFICATION_LIST, params, true, 1)
    }



    override fun onResume() {
        super.onResume()
        verification_list()
    }

}