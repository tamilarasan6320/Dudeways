package com.app.dudeways.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.front.CameraSource
import com.app.dudeways.front.FaceContourDetectorProcessor
import com.app.dudeways.front.PermissionUtil
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivitySelfiBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SelfiActivity : AppCompatActivity(), FaceContourDetectorProcessor.FaceContourDetectorListener {

    companion object {
        const val KEY_IMAGE_PATH = "image_path"
        private const val PERMISSION_CAMERA_REQUEST_CODE = 2
        private const val TAG = "SelfiActivity"
    }

    private lateinit var binding: ActivitySelfiBinding
    private var mCameraSource: CameraSource? = null
    private var mCapturedBitmap: Bitmap? = null
    private lateinit var mFaceContourDetectorProcessor: FaceContourDetectorProcessor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelfiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (PermissionUtil.isHavePermission(
                this, PERMISSION_CAMERA_REQUEST_CODE, Manifest.permission.CAMERA
            )
        ) {
            createCameraSource()
        }

        binding.tvBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivCapture.setOnClickListener {
            createSelfiePictureAndReturn()
        }
    }

    private fun createSelfiePictureAndReturn() {
        val file = File(cacheDir, "selfie.jpg")
        file.createNewFile()

        // Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        mCapturedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, bos)
        val bitmapData = bos.toByteArray()

        // Write the bytes in file
        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()

        val intent = Intent()
        intent.putExtra(KEY_IMAGE_PATH, file.absolutePath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun createCameraSource() {
        try {
            mCameraSource = CameraSource(this, binding.faceOverlay)
            mFaceContourDetectorProcessor = FaceContourDetectorProcessor(this, false)
            mCameraSource?.setMachineLearningFrameProcessor(mFaceContourDetectorProcessor)
            startCameraSource()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create camera source: ${e.message}")
        }
    }

    private fun startCameraSource() {
        mCameraSource?.let {
            try {
                binding.cameraPreview.start(it, binding.faceOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source. ${e.message}")
                releaseCamera()
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        binding.cameraPreview.stop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }

    private fun releaseCamera() {
        try {
            mCameraSource?.release()
            mCameraSource = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing camera source: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createCameraSource()
            } else {
                Log.e(TAG, "Camera permission denied")
                onBackPressed()
            }
        }
    }

    override fun onCapturedFace(originalCameraImage: Bitmap) {
        mCapturedBitmap = originalCameraImage
        binding.ivCapture.alpha = 1F
        binding.ivCapture.isEnabled = true
    }

    override fun onNoFaceDetected() {
        mCapturedBitmap = null
        binding.ivCapture.alpha = 0.3F
        binding.ivCapture.isEnabled = false
    }
}
