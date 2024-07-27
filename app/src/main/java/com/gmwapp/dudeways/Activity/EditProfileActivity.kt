package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityEditProfileBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject


class EditProfileActivity : BaseActivity() {

    lateinit var binding: ActivityEditProfileBinding
    lateinit var activity: Activity
    lateinit var session: Session

    var professions = listOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)


        binding.etMobileNumber.setText(session.getData(Constant.MOBILE))
        binding.etName.setText(session.getData(Constant.NAME))
        binding.etEmail.setText(session.getData(Constant.EMAIL))
        binding.etAge.setText(session.getData(Constant.AGE))
        binding.etProfession.setText(session.getData(Constant.PROFESSION))
        binding.etState.setText(session.getData(Constant.STATE))
        binding.etcity.setText(session.getData(Constant.CITY))
        binding.etIntroduction.setText(session.getData(Constant.INTRODUCTION))


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

        profession_list()
        binding.tvSkip.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.ibBack.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSave.setOnClickListener {
            if (binding.etName.text.toString().isEmpty()) {
                binding.etName.error = "Please enter name"
                return@setOnClickListener
            } else if (binding.etName.text.toString().length < 4) {
                binding.etName.error = "Name should be at least 4 characters"
                return@setOnClickListener
            } else if (binding.etEmail.text.toString().isEmpty()) {
                binding.etEmail.error = "Please enter email"
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()) {
                binding.etEmail.error = "Enter a valid Email address"
                return@setOnClickListener
            }  else if (binding.etProfession.text.toString().isEmpty()) {
                binding.etProfession.error = "Please enter profession"
                return@setOnClickListener
            } else if (binding.etState.text.toString().isEmpty()) {
                binding.etState.error = "Please enter state"
                return@setOnClickListener
            } else if (binding.etcity.text.toString().isEmpty()) {
                binding.etcity.error = "Please enter city"
                return@setOnClickListener
            }else if (binding.etIntroduction.text.toString().length < 15) {
                binding.etIntroduction.error = "Introduction should be at least 15 characters"
                return@setOnClickListener
            }
            else {
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
            "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa",
            "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala",
            "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland",
            "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
            "Uttar Pradesh", "Uttarakhand", "West Bengal"
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

        val adapter = ProfessionAdapter(professions) { selectedProfession ->
            binding.etProfession.setText(selectedProfession)
            binding.cardProfession.visibility = View.GONE
            binding.etProfession.error = null
        }
        binding.rvProfession.adapter = adapter
        binding.rvProfession.layoutManager = LinearLayoutManager(this)
    }



    private fun register() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.MOBILE] = session.getData(Constant.MOBILE)
        params[Constant.NAME] = binding.etName.text.toString()
        params[Constant.EMAIL] = binding.etEmail.text.toString()
        params[Constant.GENDER]= session.getData(Constant.GENDER)
        params[Constant.AGE] = binding.etAge.text.toString()
        params[Constant.PROFESSION] = binding.etProfession.text.toString()
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
                        session.setData(Constant.NAME, jsonobj.getString(Constant.NAME))
                        session.setData(Constant.UNIQUE_NAME, jsonobj.getString(Constant.UNIQUE_NAME))
                        session.setData(Constant.EMAIL, jsonobj.getString(Constant.EMAIL))
                        session.setData(Constant.AGE, jsonobj.getString(Constant.AGE))
                        session.setData(Constant.GENDER, jsonobj.getString(Constant.GENDER))
                        session.setData(Constant.PROFESSION, jsonobj.getString(Constant.PROFESSION))
                        session.setData(Constant.STATE, jsonobj.getString(Constant.STATE))
                        session.setData(Constant.CITY, jsonobj.getString(Constant.CITY))
                        session.setData(Constant.PROFILE, jsonobj.getString(Constant.PROFILE))
                        session.setData(Constant.MOBILE, jsonobj.getString(Constant.MOBILE))
                        session.setData(Constant.REFER_CODE, jsonobj.getString(Constant.REFER_CODE))
                        session.setData(Constant.REFERRED_BY, jsonobj.getString(Constant.REFERRED_BY))
                        Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, HomeActivity::class.java)
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
        }, activity, Constant.UPDATE_USERS, params, true, 1)
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
                        professions = mutableListOf()
                        for (i in 0 until jsonArray.length()) {
                            val professionObject = jsonArray.getJSONObject(i)
                            val profession = professionObject.getString("profession")
                            professions += profession
                        }
                        // Now professions list is populated, you can use it wherever needed
                      //  Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
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