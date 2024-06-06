package com.app.dudeways.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityProfileViewBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class ProfileViewActivity : AppCompatActivity() {

    lateinit var binding:ActivityProfileViewBinding
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_view)
        binding = ActivityProfileViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        session = Session(activity)

        // call requestIdToken as follows
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.ivAddProfile.setOnClickListener {
            isCameraRequest = false
            pickImageFromGallery()
        }

        binding.ivEdit.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.ivCamera.setOnClickListener {
            isCameraRequest = true
            pickImageFromGallery()
        }


        Glide.with(activity).load(session.getData(Constant.COVER_IMG)).placeholder(R.drawable.placeholder_bg).into(binding.ivCover)
        Glide.with(activity).load(session.getData(Constant.PROFILE)).placeholder(R.drawable.profile_placeholder).into(binding.civProfile)

        binding.tvProfessional.text = session.getData(Constant.PROFESSION)
        binding.tvCity.text = session.getData(Constant.CITY)
        binding.tvState.text = session.getData(Constant.STATE)
        binding.tvGender.text = session.getData(Constant.GENDER)
        binding.tvName.text = session.getData(Constant.NAME)
        binding.tvUsername.text = "@"+session.getData(Constant.UNIQUE_NAME)
        binding.tvPlace.text = session.getData(Constant.CITY) + ", " + session.getData(Constant.STATE)


        val gender = session.getData(Constant.GENDER)
        val age = session.getData(Constant.AGE)
        binding.ivAge.text = age

        if(gender == "male") {
            binding.ivGender.setBackgroundDrawable(resources.getDrawable(R.drawable.male_ic))
        }
        else {
            binding.ivGender.setBackgroundDrawable(resources.getDrawable(R.drawable.female_ic))
        }

        if (gender == "male") {
            binding.ivGenderColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue_200))
        } else {
            binding.ivGenderColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary))
        }






        binding.rlMytrips.setOnClickListener {
            val intent = Intent(activity, MytripsActivity::class.java)
            startActivity(intent)
        }


        binding.ivBack.setOnClickListener{
            onBackPressed()
        }

        binding.rlStorepoints.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)

            val dialogBuilder = AlertDialog.Builder(activity)
                .setView(dialogView)
                .create()
            val title = dialogView.findViewById<TextView>(R.id.dialog_title)
            val btnPurchase = dialogView.findViewById<Button>(R.id.btnPurchase)
            val btnFreePoints = dialogView.findViewById<Button>(R.id.btnFreePoints)

            btnPurchase.setOnClickListener {
                val intent = Intent(activity, PurchasepointActivity::class.java)
                startActivity(intent)
                dialogBuilder.dismiss()
            }

            btnFreePoints.setOnClickListener {
                val intent = Intent(activity, FreePointsActivity::class.java)
                startActivity(intent)
                dialogBuilder.dismiss()
            }




            dialogBuilder.show()
        }


        binding.rlDeactiveaccount.setOnClickListener {
            val intent = Intent(activity, DeactivateActivity::class.java)
            startActivity(intent)
        }

        binding.rlCustomerSupport.setOnClickListener {
            val intent = Intent(activity, CustomerSupportActivity::class.java)
            startActivity(intent)
        }

        binding.rlVerificationBadge.setOnClickListener {
            val intent = Intent(activity, IdverficationActivity::class.java)
            startActivity(intent)
        }

        binding.rlLogout.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                session.logoutUser(activity)
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

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
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                imageUri = data?.data
                if (isCameraRequest) {
                    // Configure CropImage for rectangular crop
                    CropImage.activity(imageUri)
                        .setAspectRatio(4, 1) // Set aspect ratio for a rectangle
                        .setCropShape(CropImageView.CropShape.RECTANGLE) // Set crop shape to rectangle
                        .start(this)
                } else {
                    // Configure CropImage for circular crop
                    CropImage.activity(imageUri)
                        .setAspectRatio(1, 1) // Set aspect ratio to 1:1 for a square crop
                        .setCropShape(CropImageView.CropShape.OVAL) // Set crop shape to oval
                        .start(this)
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
                        session.setData(Constant.USER_ID, jsonobj.getString(Constant.ID))
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
                        session.setData(Constant.PROFILE, jsonobj.getString(Constant.PROFILE))
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
                        session.setData(Constant.USER_ID, jsonobj.getString(Constant.ID))
                        session.setData(Constant.COVER_IMG, jsonobj.getString(Constant.COVER_IMG))
                        Glide.with(activity).load(session.getData(Constant.COVER_IMG)).placeholder(R.drawable.cover_img).into(binding.ivCover)

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

}