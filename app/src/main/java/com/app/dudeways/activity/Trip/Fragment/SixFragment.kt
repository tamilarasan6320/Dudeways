package com.app.dudeways.activity.Trip.Fragment

import android.app.Activity
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
import com.app.dudeways.activity.HomeActivity
import com.app.dudeways.activity.Trip.StarttripActivity
import com.app.dudeways.databinding.FragmentSixBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class SixFragment : Fragment() {

    lateinit var binding: FragmentSixBinding
    lateinit var activity: Activity
    lateinit var session: Session

    var filePath1: String? = null
    var imageUri: Uri? = null

    private val REQUEST_IMAGE_GALLERY = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSixBinding.inflate(layoutInflater)
        activity = requireActivity()
        session = Session(activity)

        (activity as StarttripActivity).binding.tvTitle.visibility = View.INVISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Start Trip"

        binding.ivAddProof1.setOnClickListener {
            pickImageFromGallery()
        }


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
                CropImage.activity(imageUri)
                    // calculate the aspect ratio x of the image
                    // calculate the aspect ratio y of the image
                    .setAspectRatio(5, 3) // Set aspect ratio to 4:3 for full width and height
                    .setCropShape(CropImageView.CropShape.RECTANGLE) // Set crop shape to rectangle
                    .start(requireContext(), this)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(activity, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.ivProof1.setImageBitmap(myBitmap)
                        binding.ivAddProof1.visibility = View.GONE

                    }
                }
            }
        }
    }



    fun addtripImage(id: String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.TRIP_ID] = id.toString()
        val FileParams: MutableMap<String, String> = HashMap()
        FileParams[Constant.TRIP_IMAGE] = filePath1!!
        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {


                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        activity.finish()

                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "JSON Parsing Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity," $result" , Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.UPDATE_TRIP_IMAGE, params, FileParams)
    }
    fun addtrip() {
        val trip_type = ""
        if (session.getData(Constant.TRIP_TYPE) == "0") {
            trip_type == "Road Trip"
        } else if (session.getData(Constant.TRIP_TYPE) == "1") {
            trip_type == "Adventure Trip"
        } else if (session.getData(Constant.TRIP_TYPE) == "2") {
            trip_type == "Explore Cities"
        } else if (session.getData(Constant.TRIP_TYPE) == "3") {
            trip_type == "Airport Flyover"
        }

        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.TRIP_TYPE] = trip_type.toString()
        params[Constant.TRIP_FROM_DATE] = session.getData(Constant.TRIP_FROM_DATE)
        params[Constant.TRIP_TO_DATE] = session.getData(Constant.TRIP_TO_DATE)
        params[Constant.TRIP_TITLE] = session.getData(Constant.TRIP_TITLE)
        params[Constant.TRIP_DESCRIPTION] = session.getData(Constant.TRIP_DESCRIPTION)
        params[Constant.TRIP_LOCATION] = session.getData(Constant.TRIP_LOCATION)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)
                        session.setData(Constant.USER_ID, jsonobj.getString(Constant.ID))

                        val id = jsonobj.getString(Constant.ID)
                        addtripImage(id)

                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "JSON Parsing Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity," $result" , Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.ADD_TRIP, params, true,1)
    }

}
