package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityProfileDetailsBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Field

class ProfileDetailsActivity : BaseActivity() {

    lateinit var binding: ActivityProfileDetailsBinding
    lateinit var activity: Activity
    lateinit var session: Session

    var professions = mutableListOf<Pair<String, String>>() // Pair of (professionName, professionId)

    var select_option = "0"
    var gender = ""

    var profession_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        // Set custom cursor color
        setCursorDrawableColor(binding.etName, R.drawable.color_cursor)
        setCursorDrawableColor(binding.etEmail, R.drawable.color_cursor)
        setCursorDrawableColor(binding.etMobileNumber, R.drawable.color_cursor)
        setCursorDrawableColor(binding.etAge, R.drawable.color_cursor)
        setCursorDrawableColor(binding.etProfession, R.drawable.color_cursor)
        setCursorDrawableColor(binding.etcity, R.drawable.color_cursor)
        setCursorDrawableColor(binding.etState, R.drawable.color_cursor)
        setCursorDrawableColor(binding.etRefferCode, R.drawable.color_cursor)

        profession_list()

        binding.etIntroduction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etIntroduction.lineCount > 3) {
                    val text = s.toString().substring(0, binding.etIntroduction.selectionEnd - 1)
                    binding.etIntroduction.setText(text)
                    binding.etIntroduction.setSelection(binding.etIntroduction.text!!.length)
                }
            }
        })

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
                binding.etName.error = "Name should be at least 4 characters"
                return@setOnClickListener
            } else if (select_option == "0") {
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
            } else if (binding.etIntroduction.text.toString().isEmpty()) {
                binding.etIntroduction.error = "Please enter introduction"
                return@setOnClickListener
            } else if (binding.etIntroduction.text.toString().length < 15) {
                binding.etIntroduction.error = "Introduction should be at least 15 characters"
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

        binding.btnNext.setOnClickListener {
            binding.nsProfileDetails.visibility = View.VISIBLE
            binding.llDescribtion.visibility = View.GONE
        }
    }

    private fun showProfessionDialogstate(etState: EditText) {
        val State = listOf(
            "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa",
            "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala",
            "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland",
            "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
            "Uttar Pradesh", "Uttarakhand", "West Bengal"
        )
        val adapter = ProfessionAdapter(State) { selectedState ->
            binding.etState.setText(selectedState)
            binding.cardstate.visibility = View.GONE
            binding.etState.error = null
        }
        binding.rvState.adapter = adapter
        binding.rvState.layoutManager = LinearLayoutManager(this)
    }

    private fun showProfessionDialog(etProfession: EditText) {
        val adapter = ProfessionAdapter(professions.map { it.first }) { selectedProfession ->
            binding.etProfession.setText(selectedProfession)
            profession_id = professions.first { it.first == selectedProfession }.second
            binding.cardProfession.visibility = View.GONE
            binding.etProfession.error = null
        }
        binding.rvProfession.adapter = adapter
        binding.rvProfession.layoutManager = LinearLayoutManager(this)
    }

    private fun setCursorDrawableColor(editText: EditText, drawableRes: Int) {
        try {
            val drawable: Drawable = resources.getDrawable(drawableRes, null)
            val field: Field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true
            field.set(editText, drawable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun register() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.NAME] = binding.etName.text.toString()
        params[Constant.EMAIL] = session.getData(Constant.EMAIL)
        params[Constant.AGE] = binding.etAge.text.toString()
        params[Constant.GENDER] = gender.toString()
        params[Constant.PROFESSION_ID] = profession_id // Pass the profession ID
        params[Constant.STATE] = binding.etState.text.toString()
        params[Constant.CITY] = binding.etcity.text.toString()
        params[Constant.INTRODUCTION] = binding.etIntroduction.text.toString()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)
                        session.setData(Constant.USER_ID, jsonobj.getString(Constant.ID))
                        session.setData(Constant.NAME, jsonobj.getString(Constant.NAME))
                        session.setData(Constant.UNIQUE_NAME, jsonobj.getString(Constant.UNIQUE_NAME))
                        session.setData(Constant.EMAIL, jsonobj.getString(Constant.EMAIL))
                        session.setData(Constant.AGE, jsonobj.getString(Constant.AGE))
                        session.setData(Constant.GENDER, jsonobj.getString(Constant.GENDER))
                        session.setData(Constant.PROFESSION, jsonobj.getString(Constant.PROFESSION))
                        session.setData(Constant.STATE, jsonobj.getString(Constant.STATE))
                        session.setData(Constant.CITY, jsonobj.getString(Constant.CITY))
                        session.setData(Constant.MOBILE, jsonobj.getString(Constant.MOBILE))
                        session.setData(Constant.REFER_CODE, jsonobj.getString(Constant.REFER_CODE))
                        session.setData(Constant.REFERRED_BY, jsonobj.getString(Constant.REFERRED_BY))
                        session.setData(Constant.INTRODUCTION, jsonobj.getString(Constant.INTRODUCTION))
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

    private fun profession_list() {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        val jsonArray = jsonObject.getJSONArray("data")

                        // Clear previous data
                        professions.clear()
                        for (i in 0 until jsonArray.length()) {
                            val professionObject = jsonArray.getJSONObject(i)
                            val profession = professionObject.getString("profession")
                            val id = professionObject.getString("id")
                            professions.add(profession to id) // Store as Pair(professionName, professionId)
                        }
                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.PROFESSION_LIST, params, true, 1)
    }
}
