package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityStage2Binding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject

class Stage2Activity : BaseActivity() {

    lateinit var binding: ActivityStage2Binding
    lateinit var activity: Activity
    lateinit var session: Session
    var imageUri: Uri? = null
    var isImageUploaded = false
    var filePath1: String? = null
    companion object {
        const val SELFIE_REQUEST_CODE = 1
    }

    private val selfiLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imagePath = result.data?.getStringExtra(SelfiActivity.KEY_IMAGE_PATH)
            if (imagePath != null) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                binding.ivProof1.setImageBitmap(bitmap)
                binding.ivAddProof1.visibility = View.GONE
                isImageUploaded = true
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStage2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        binding.cvProof1.setOnClickListener {
            startActivityForResult(Intent(this, SelfiActivity::class.java), SELFIE_REQUEST_CODE)
        }

        binding.btnUpload.setOnClickListener {
            if (isImageUploaded) {
                // Proceed to the next stage
                    verifyFrontImage()

            } else {
                Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun verifyFrontImage() {
        Toast.makeText(this, "Verifying image...", Toast.LENGTH_SHORT).show()
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        val fileParams: MutableMap<String, String> = HashMap()
        fileParams[Constant.SELFIE_IMAGE] = filePath1!!

        ApiConfig.requestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        val dataArray = jsonObject.getJSONArray("data")
                        if (dataArray.length() > 0) {
                            val dataObject = dataArray.getJSONObject(0)
                            val selfieImageUrl = dataObject.getString("selfie_image")


                            session.setData(Constant.SELFIE_IMAGE, selfieImageUrl)


                            startActivity(Intent(this, Stage1Activity::class.java))
                            finish()


                            Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                        }


                    } else {
                        session.setData(Constant.SELFIE_IMAGE, "")
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(activity, "$result", Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.VERIFY_SELFIE_IMAGE, params, fileParams)
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELFIE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imagePath = data?.getStringExtra(SelfiActivity.KEY_IMAGE_PATH)
          //  Toast.makeText(this, imagePath, Toast.LENGTH_SHORT).show()
            val bmImg = BitmapFactory.decodeFile(imagePath)
            filePath1 = imagePath
            binding.ivProof1.setImageBitmap(bmImg)
            isImageUploaded = true
            binding.ivAddProof1.visibility = View.GONE
        }
    }
}
