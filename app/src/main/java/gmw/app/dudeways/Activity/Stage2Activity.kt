package gmw.app.dudeways.Activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import gmw.app.dudeways.R
import gmw.app.dudeways.databinding.ActivityStage2Binding
import gmw.app.dudeways.helper.Constant
import gmw.app.dudeways.helper.Session

class Stage2Activity : AppCompatActivity() {

    lateinit var binding: ActivityStage2Binding
    lateinit var activity: Activity
    lateinit var session: Session
    var imageUri: Uri? = null

    // boolean to check if the image is uploaded
    var isImageUploaded = false

    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

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
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
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
                startActivity(Intent(this, Stage1Activity::class.java))
                session.setData(Constant.PROOF1, "1")
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
                cameraLauncher.launch(takePictureIntent)
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
}
