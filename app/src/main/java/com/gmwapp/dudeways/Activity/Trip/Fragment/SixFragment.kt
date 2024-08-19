package com.gmwapp.dudeways.Activity.Trip.Fragment

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
import com.gmwapp.dudeways.Activity.Trip.StarttripActivity
import com.gmwapp.dudeways.Activity.Trip.TripCompletedActivity
import com.gmwapp.dudeways.databinding.FragmentSixBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
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
    var trip_type: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSixBinding.inflate(layoutInflater)
        activity = requireActivity()
        session = Session(activity)


        if (session.getData(Constant.PROFILE) != "") {
            binding.cbUseProfileImage.visibility = View.VISIBLE
        }
        else{
            binding.cbUseProfileImage.visibility = View.GONE
        }

        (activity as StarttripActivity).binding.btnNext.text = "Start Trip"

        binding.ivAddProof1.setOnClickListener {
            pickImageFromGallery()
        }

        binding.ivProof1.setOnClickListener {
            pickImageFromGallery()
        }

        binding.ivPost.setOnClickListener {
            pickImageFromGallery()
        }

        when (session.getData(Constant.TRIP_TYPE)) {
            "0" -> trip_type = "Road Trip"
            "1" -> trip_type = "Adventure Trip"
            "2" -> trip_type = "Explore Cities"
            "3" -> trip_type = "Airport Flyover"
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
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(requireContext(), this)

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(activity, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.ivPost.setImageBitmap(myBitmap)
                        binding.ivAddProof1.visibility = View.GONE
                        binding.ivProof1.visibility = View.INVISIBLE
                    //    binding.rlProfile.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun addtripImage(id: String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.TRIP_ID] = id
        val fileParams: MutableMap<String, String> = HashMap()
        if (!filePath1.isNullOrEmpty()) {
            fileParams[Constant.TRIP_IMAGE] = filePath1!!
        }
        ApiConfig.RequestToVolleyMulti({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val intent = Intent(activity, TripCompletedActivity::class.java)
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
                Toast.makeText(activity, "$result", Toast.LENGTH_SHORT).show()
            }
        }, activity, Constant.UPDATE_TRIP_IMAGE, params, fileParams)
    }

    fun addtrip() {

        val profileImage: String

        // Check if either the checkbox is checked or an image is selected
        if (binding.cbUseProfileImage.isChecked) {
            profileImage = "1"
            filePath1 = session.getData(Constant.PROFILE_IMAGE)
        } else {
            profileImage = "0"
        }

        if (!binding.cbUseProfileImage.isChecked && filePath1.isNullOrEmpty()) {
            Toast.makeText(activity, "Please select an image to upload or use your profile image.", Toast.LENGTH_SHORT).show()
            return
        }

        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.TRIP_TYPE] = trip_type.toString()
        params[Constant.TRIP_FROM_DATE] = session.getData(Constant.TRIP_FROM_DATE)
        params[Constant.TRIP_TO_DATE] = session.getData(Constant.TRIP_TO_DATE)
        params[Constant.TRIP_TITLE] = session.getData(Constant.TRIP_TITLE)
        params[Constant.TRIP_DESCRIPTION] = session.getData(Constant.TRIP_DESCRIPTION)
        params[Constant.TRIP_LOCATION] = session.getData(Constant.TRIP_LOCATION)
        params[Constant.PROFILE_IMAGE] = profileImage.toString()

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val id = jsonObject.getJSONObject(Constant.DATA).getString(Constant.ID)

                        if (binding.cbUseProfileImage.isChecked)
                        {
                            val intent = Intent(activity, TripCompletedActivity::class.java)
                            startActivity(intent)
                            activity.finish()
                            Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()                        }
                        else{
                            addtripImage(id)
                        }

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
        }, activity, Constant.ADD_TRIP, params, true, 1)
    }
}
