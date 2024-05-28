package gmw.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImage
import gmw.app.dudeways.R
import gmw.app.dudeways.databinding.ActivityStage3Binding
import gmw.app.dudeways.helper.Constant
import gmw.app.dudeways.helper.Session
import java.io.File

class Stage3Activity : AppCompatActivity() {

    lateinit var binding: ActivityStage3Binding
    lateinit var activity: Activity
    lateinit var session: Session

    var filePath1: String? = null
    var imageUri: Uri? = null

    private val REQUEST_IMAGE_GALLERY = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stage3)

        binding = ActivityStage3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.cvFrontproof.setOnClickListener {
            session.setData(Constant.FrontPROOF, "0")
            pickImageFromGallery()
        }

        binding.cvBackproof.setOnClickListener {
            session.setData(Constant.BackPROOF, "0")
            pickImageFromGallery()
        }

        binding.btnUpload.setOnClickListener {
            if (session.getData(Constant.FrontPROOF) == "1" && session.getData(Constant.BackPROOF) == "1") {
                val intent = Intent(activity, Stage1Activity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(activity, "Please upload both front and back proof", Toast.LENGTH_SHORT).show()

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
                    }
                    else if (session.getData(Constant.BackPROOF) == "0") {
                        binding.ivBackproof.setImageBitmap(myBitmap)
                        binding.ibBackproof.visibility = View.GONE
                        session.setData(Constant.BackPROOF, "1")
                    }

                }
            }
        }
    }
}