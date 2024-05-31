package com.app.dudeways.Activity.Trip.Fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.dudeways.Activity.Trip.StarttripActivity
import com.app.dudeways.databinding.FragmentSixBinding
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
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

        (activity as StarttripActivity).binding.tvTitle.visibility = View.GONE
        (activity as StarttripActivity).binding.btnNext.text = "Start Trip"
        (activity as StarttripActivity).binding.btnBack.visibility = View.VISIBLE

        binding.ivAddProof1.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnNext.setOnClickListener {
            addtrip()
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                imageUri = data?.data
                CropImage.activity(imageUri)
                    .start(requireContext(), this)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)!!
                filePath1 = result.getUriFilePath(requireContext(), true)
                val imgFile: File = File(filePath1)
                if (imgFile.exists()) {
                    val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)


                        binding.ivProof1.setImageBitmap(myBitmap)
                        binding.ivAddProof1.visibility = View.GONE

                }
            }
        }
    }



    private fun addtrip() {
        // Your add trip implementation here
    }
}
