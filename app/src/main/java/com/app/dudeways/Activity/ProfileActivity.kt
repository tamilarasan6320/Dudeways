package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.databinding.ActivityProfileBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    lateinit var activity: Activity
    lateinit var session: Session


    var filePath1: String? = null
    var imageUri: Uri? = null

    private val REQUEST_IMAGE_GALLERY = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        binding.btnUpdateProfile.setOnClickListener {

            if (session.getBoolean(Constant.PROFILE) == true) {
//                Toast.makeText(this, "lease select profile image", Toast.LENGTH_SHORT).show()
//                val intent = Intent(activity, HomeActivity::class.java)
//                startActivity(intent)
//                finish()
           uploadProfile()
            } else {
                Toast.makeText(this, "Please select profile image", Toast.LENGTH_SHORT).show()
            }

        }

        binding.ivProfile.setOnClickListener {
            pickImageFromGallery()
        }

        binding.ibBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    // onbackpressed
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun order(bookid: String) {
        val params: MutableMap<String, String> = java.util.HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.BOOKID] = bookid
        val FileParams: MutableMap<String, String> = java.util.HashMap()
        FileParams[Constant.IMAGE] = filePath1!!


        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(
                            activity,
                            "" + jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()



                        //                        Intent intent = new Intent(activity, PaymentStatusActivity.class);
//                        intent.putExtra("id", bookid);
//                        intent.putExtra("price", price);
//                        intent.putExtra("name", name);
//                        intent.putExtra("sub_name", sub_name);
//                        intent.putExtra("image", image);
//                        intent.putExtra("code", code);
//                        intent.putExtra("publication", publication);
//                        intent.putExtra("regulation", regulation);
//                        startActivity(intent);
                    } else {
                        Toast.makeText(
                            activity,
                            "" + jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()



                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.ORDER, params, FileParams)
    }


    private fun uploadProfile() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        val FileParams: MutableMap<String, String> = HashMap()
        FileParams[Constant.IMAGE] = filePath1!!
        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)
                        session.setData(Constant.PROFILE, jsonobj.getString(Constant.PROFILE))
                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
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
                CropImage.activity(imageUri)
                    .setAspectRatio(1, 1) // Set aspect ratio to 1:1 for a square crop
                    .setCropShape(CropImageView.CropShape.OVAL) // Set crop shape to oval
                    .start(this)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(activity, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.ivProfile.setImageBitmap(myBitmap)
                        binding.ivEditProfile.visibility = View.GONE
                        session!!.setBoolean(Constant.PROFILE, true)
                    }
                }
            }
        }
    }
}