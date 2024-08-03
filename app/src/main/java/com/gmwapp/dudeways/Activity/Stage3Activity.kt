package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityStage3Binding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class Stage3Activity : BaseActivity() {

    private lateinit var binding: ActivityStage3Binding
    private lateinit var activity: Activity
    private lateinit var session: Session

    private var filePath1: String? = null
    private var imageUri: Uri? = null // Define imageUri here
    private lateinit var imageBitmap: Bitmap

    private val REQUEST_IMAGE_GALLERY = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStage3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)



        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        session.setData(Constant.FrontPROOF, "0")
        session.setData(Constant.BackPROOF, "0")

        binding.cvFrontproof.setOnClickListener {
            session.setData(Constant.FrontPROOF, "0")
            pickImageFromGallery()
        }

        binding.cvBackproof.setOnClickListener {
            session.setData(Constant.BackPROOF, "0")
            pickImageFromGallery()
        }

        binding.btnUpload.setOnClickListener {

            if (session.getData(Constant.FrontPROOF) == "0") {
                Toast.makeText(activity, "Please upload front proof", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (session.getData(Constant.BackPROOF) == "0") {
                Toast.makeText(activity, "Please upload back proof", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                verifyFrontImage(imageBitmap)
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
                CropImage.activity(imageUri)
                    .start(activity)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)!!
                filePath1 = result.getUriFilePath(activity, true)
                val imgFile: File = File(filePath1)
                if (imgFile.exists()) {
                    val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                    if (session.getData(Constant.FrontPROOF) == "0") {
                        binding.ivFrontproof.setImageBitmap(myBitmap)
                        binding.ibFrontproof.visibility = View.GONE
                        session.setData(Constant.FrontPROOF, "1")
                        imageBitmap = myBitmap
                    } else if (session.getData(Constant.BackPROOF) == "0") {
                        binding.ivBackproof.setImageBitmap(myBitmap)
                        binding.ibBackproof.visibility = View.GONE
                        session.setData(Constant.BackPROOF, "1")
                        // You might want to assign the back proof bitmap here if needed
                    }
                }
            }
        }
    }

    private fun verifyFrontImage(bitmap: Bitmap) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)

        // Convert Bitmap to File
        val file = bitmapToFile(bitmap)

        // Add the file path to your params
        val fileParams: MutableMap<String, String> = HashMap()
        fileParams[Constant.FRONT_IMAGE] = file.path

        // Send the API request
        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {



                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)
                            val front_image = jsonobj.getString("front_image")
                        session.setData(Constant.FRONT_IMAGE, front_image)


                            verifyBackImage(bitmap)


                        // Verify the back image here
                      //  Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(activity, "2" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "Error parsing response", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.VERIFY_FRONT_IMAGE, params, fileParams)
    }

    private fun verifyBackImage(bitmap: Bitmap) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)

        // Convert Bitmap to File
        val file = bitmapToFile(bitmap)

        // Add the file path to your params
        val fileParams: MutableMap<String, String> = HashMap()
        fileParams[Constant.BACK_IMAGE] = file.path

        // Send the API request
        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)

                            val back_image = jsonobj.getString("back_image")
                            session.setData(Constant.BACK_IMAGE, back_image)
                            startActivity(Intent(this, PurchaseverifybuttonActivity::class.java))
                            finish()



                       // Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "1" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "1Error parsing response", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.VERIFY_BACK_IMAGE, params, fileParams)
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        // Create a file in the cache directory
        val file = File(activity.cacheDir, "temp_image.jpg")

        // Write the bitmap to the file
        file.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        return file
    }
}
