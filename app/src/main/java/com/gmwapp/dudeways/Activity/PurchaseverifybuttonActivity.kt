package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.gmwapp.dudeways.Activity.Trip.TripCompletedActivity
import com.gmwapp.dudeways.Adapter.PlanlistAdapter
import com.gmwapp.dudeways.Model.Planlist
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityPurchaseverifybuttonBinding
import com.gmwapp.dudeways.gateway.MainActivity
import com.gmwapp.dudeways.gateway.PurchaseVerifyActivity
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class PurchaseverifybuttonActivity : BaseActivity() {

    lateinit var binding: ActivityPurchaseverifybuttonBinding
    lateinit var activity: Activity
    lateinit var session: Session
    lateinit var amount:String
    lateinit var id:String

    private val REQUEST_IMAGE_GALLERY = 2

    var imageUri: Uri? = null
    var filePath1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseverifybuttonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)


        val upiId = session.getData(Constant.UPI_ID)
        binding.tvUpiId.text = upiId


        binding.rvplan.layoutManager = LinearLayoutManager(activity)

        binding.ivBack.setOnClickListener {
            onBackPressed()
            finish()
        }

        binding.btnCopyUpi.setOnClickListener {
            copyTextToClipboard(upiId)
        }

        binding.btnUploadScreenshots.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnSendReview.setOnClickListener{
            uploadPaymentImage()
//            val intent = Intent(activity, PurchaseVerifyActivity::class.java)
//            intent.putExtra("id", id)
//            session.setData(Constant.AMOUNT,amount)
//            startActivity(intent)
        }

        // Call the API to fetch the plan list
        planlist()
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
                CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(this)

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(activity, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.btnUploadScreenshots.text = getString(R.string.screenshot_uploaded)
                        binding.btnUploadScreenshots.setTextColor(ContextCompat.getColor(this, R.color.darkGreen))
                    }
                }
            }
        }
    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Refer Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    private fun planlist() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray: JSONArray = jsonObject.getJSONArray(Constant.DATA)
                        val gson = Gson()
                        val planList: ArrayList<Planlist> = ArrayList()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val plan: Planlist = gson.fromJson(jsonObject1.toString(), Planlist::class.java)
                            planList.add(plan)
                        }

                        // Set up the RecyclerView with the adapter
                        val adapter = PlanlistAdapter(activity, planList) { plan ->
                        //    showToast("${plan.plan_name} selected")

                            amount = plan.price.toString()
                            id = plan.id.toString()


                        }
                        binding.rvplan.adapter = adapter

                    } else {
                        Toast.makeText(activity, "Error: ${jsonObject.getString(Constant.MESSAGE)}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "JSON error: ${e.toString()}", Toast.LENGTH_SHORT).show()
                }
            }
        }, activity, Constant.PLAN_LIST, params, true)
    }

    private fun uploadPaymentImage() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        val fileParams: MutableMap<String, String> = HashMap()
        if (!filePath1.isNullOrEmpty()) {
            fileParams[Constant.PAYMENT_IMAGE] = filePath1!!
        } else {
            Toast.makeText(activity, "Please upload payment screenshot", Toast.LENGTH_SHORT).show()
        }
        ApiConfig.requestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "JSON Parsing Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "$result", Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.PAYMENT_IMAGE_API, params, fileParams)
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}
