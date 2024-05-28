package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityProfileDetailsBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject


class ProfileDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileDetailsBinding
    lateinit var activity: Activity
    lateinit var session: Session

    var select_option = "0"
    var gender = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)


        binding.llMale.setOnClickListener {
            binding.llMale.backgroundTintList = resources.getColorStateList(R.color.primary)
            binding.llFemale.backgroundTintList =
                resources.getColorStateList(R.color.primary_extra_light)
            binding.llOthers.backgroundTintList =
                resources.getColorStateList(R.color.primary_extra_light)
            binding.tvMale.setTextColor(resources.getColor(R.color.white))
            binding.tvFemale.setTextColor(resources.getColor(R.color.black))
            binding.tvOthers.setTextColor(resources.getColor(R.color.black))
            select_option = "1"
            gender = "male"
        }
        binding.llFemale.setOnClickListener {
            binding.llFemale.backgroundTintList = resources.getColorStateList(R.color.primary)
            binding.llMale.backgroundTintList =
                resources.getColorStateList(R.color.primary_extra_light)
            binding.llOthers.backgroundTintList =
                resources.getColorStateList(R.color.primary_extra_light)
            binding.tvMale.setTextColor(resources.getColor(R.color.black))
            binding.tvFemale.setTextColor(resources.getColor(R.color.white))
            binding.tvOthers.setTextColor(resources.getColor(R.color.black))
            select_option = "2"
            gender = "female"
        }
        binding.llOthers.setOnClickListener {
            binding.tvMale.setTextColor(resources.getColor(R.color.black))
            binding.tvFemale.setTextColor(resources.getColor(R.color.black))
            binding.tvOthers.setTextColor(resources.getColor(R.color.white))
            binding.llOthers.backgroundTintList = resources.getColorStateList(R.color.primary)
            binding.llMale.backgroundTintList =
                resources.getColorStateList(R.color.primary_extra_light)
            binding.llFemale.backgroundTintList =
                resources.getColorStateList(R.color.primary_extra_light)
            select_option = "3"
            gender = "others"
        }




        binding.btnSave.setOnClickListener {

            if (binding.etName.text.toString().isEmpty()) {
                binding.etName.error = "Please enter name"
                return@setOnClickListener
            } else if (binding.etName.text.toString().length < 4) {
                binding.etName.error = "Name should be atleast 4 characters"
                return@setOnClickListener
            } else if (binding.etEmail.text.toString().isEmpty()) {
                binding.etEmail.error = "Please enter email"
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()) {
                binding.etEmail.error = "Enter a valid Email address"
                return@setOnClickListener
            } else if (select_option.equals("0")) {
                Toast.makeText(this, "Please select Gender", Toast.LENGTH_SHORT).show()
            } else if (binding.etProfession.text.toString().isEmpty()) {
                binding.etProfession.error = "Please enter profession"
                return@setOnClickListener
            } else if (binding.etState.text.toString().isEmpty()) {
                binding.etState.error = "Please enter state"
                return@setOnClickListener
            } else if (binding.etcity.text.toString().isEmpty()) {
                binding.etcity.error = "Please enter city"
                return@setOnClickListener
            } else {
                register()

            }


        }


        binding.etProfession.setOnClickListener {
            binding.cardProfession.visibility = View.VISIBLE
            showProfessionDialog(binding.etProfession)
        }
        binding.etState.setOnClickListener {
            binding.cardstate.visibility = View.VISIBLE
            showProfessionDialogstate(binding.etState)
        }


    }

    private fun showProfessionDialogstate(etState: EditText) {
        val State = listOf(
            "Andhra Pradesh",
            "Arunachal Pradesh",
            "Assam",
            "Bihar",
            "Chhattisgarh",
            "Goa",
            "Gujarat",
            "Haryana",
            "Himachal Pradesh",
            "Jharkhand",
            "Karnataka",
            "Kerala",
            "Madhya Pradesh",
            "Maharashtra",
            "Manipur",
            "Meghalaya",
            "Mizoram",
            "Nagaland",
            "Odisha",
            "Punjab",
            "Rajasthan",
            "Sikkim",
            "Tamil Nadu",
            "Telangana",
            "Tripura",
            "Uttar Pradesh",
            "Uttarakhand",
            "West Bengal"
        )
        val adapter = ProfessionAdapter(State) { selectedProfession ->
            binding.etState.setText(selectedProfession)
            binding.cardstate.visibility = View.GONE
            binding.etState.error = null
        }
        binding.rvState.adapter = adapter
        binding.rvState.layoutManager = LinearLayoutManager(this)
    }

    private fun showProfessionDialog(etProfession: EditText) {
        val professions = listOf("Doctor", "Engineer", "Teacher", "Artist", "Lawyer")
        val adapter = ProfessionAdapter(professions) { selectedProfession ->
            binding.etProfession.setText(selectedProfession)
            binding.cardProfession.visibility = View.GONE
            binding.etProfession.error = null
        }
        binding.rvProfession.adapter = adapter
        binding.rvProfession.layoutManager = LinearLayoutManager(this)

    }


    private fun showGenderDialog(editText: EditText) {
        val genderOptions = arrayOf("Male", "Female")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Gender")
            .setItems(genderOptions) { _, which ->
                val selectedGender = genderOptions[which]
                editText.setText(selectedGender)
                //   binding.etGender.error = null
            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun unselectall() {
        binding.llMale.backgroundTintList = resources.getColorStateList(R.color.primary_extra_light)
        binding.llFemale.backgroundTintList =
            resources.getColorStateList(R.color.primary_extra_light)
        binding.llOthers.backgroundTintList =
            resources.getColorStateList(R.color.primary_extra_light)
    }




    private fun register() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.MOBILE] = session.getData(Constant.MOBILE)
        params[Constant.NAME] = binding.etName.text.toString()
        params[Constant.EMAIL] = binding.etEmail.text.toString()
        params[Constant.AGE] = binding.etAge.text.toString()
        params[Constant.GENDER] = gender.toString()
        params[Constant.PROFESSION] = binding.etProfession.text.toString()
        params[Constant.STATE] = binding.etState.text.toString()
        params[Constant.CITY] = binding.etcity.text.toString()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)
                        session.setData(Constant.USER_ID, jsonobj.getString(Constant.ID))
                        session.setData(Constant.NAME, jsonobj.getString(Constant.NAME))
                        session.setData(Constant.EMAIL, jsonobj.getString(Constant.EMAIL))
                        session.setData(Constant.AGE, jsonobj.getString(Constant.AGE))
                        session.setData(Constant.GENDER, jsonobj.getString (Constant.GENDER))
                        session.setData(Constant.PROFESSION, jsonobj.getString(Constant.PROFESSION))
                        session.setData(Constant.STATE, jsonobj.getString(Constant.STATE))
                        session.setData(Constant.CITY, jsonobj.getString(Constant.CITY))
                        session.setData(Constant.PROFILE, jsonobj.getString(Constant.PROFILE))
                        session.setData(Constant.MOBILE, jsonobj.getString(Constant.MOBILE))
                        session.setData(Constant.REFER_CODE, jsonobj.getString(Constant.REFER_CODE))
                        session.setData(Constant.REFERRED_BY, jsonobj.getString(Constant.REFERRED_BY))
                        Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, ProfileActivity::class.java)
                        startActivity(intent)
                        finish()

                        session.setBoolean("is_logged_in", true)


                    } else {
                        Toast.makeText(
                            activity,
                            "" + jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.REGISTER, params, true, 1)
    }





}