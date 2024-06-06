package com.app.dudeways.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityStage2Binding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class Stage2Activity : AppCompatActivity() {

    lateinit var binding: ActivityStage2Binding
    lateinit var activity: Activity
    lateinit var session: Session
    var imageUri: Uri? = null


    // boolean to check if the image is uploaded
    var isImageUploaded = false

    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stage2)

        binding = ActivityStage2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        // Set up the camera launcher
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                 imageBitmap = (result.data?.extras?.get("data") as? Bitmap)!!
                // Handle the image here
                binding.ivProof1.setImageBitmap(imageBitmap)
                binding.ivAddProof1.visibility = View.GONE
                isImageUploaded = true


            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.cvProof1.setOnClickListener {
            openCamera()
        }

        binding.btnUpload.setOnClickListener {
            if (isImageUploaded) {
                // Proceed to the next stage
                if (imageBitmap != null){
                    verifyFrontImage(imageBitmap)
                }


            } else {
                Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                val cameraInfo = Camera.CameraInfo()
                val numberOfCameras = Camera.getNumberOfCameras()
                var cameraId = -1
                for (i in 0 until numberOfCameras) {
                    Camera.getCameraInfo(i, cameraInfo)
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        cameraId = i
                        break
                    }
                }
                if (cameraId != -1) {
                    takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT)
                    takePictureIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", cameraId)
                    takePictureIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                    cameraLauncher.launch(takePictureIntent)
                } else {
                    Toast.makeText(this, "Front camera not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
            }
        }
    }






    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to take a selfie", Toast.LENGTH_SHORT).show()
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
        fileParams[Constant.SELFIE_IMAGE] = file.path

        // Send the API request
        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        startActivity(Intent(this, Stage1Activity::class.java))
                        finish()
                        session.setData(Constant.PROOF1, "1")
                        Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            else {
                Toast.makeText(activity," $result" , Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.VERIFY_SELFIE_IMAGE, params, fileParams)
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
