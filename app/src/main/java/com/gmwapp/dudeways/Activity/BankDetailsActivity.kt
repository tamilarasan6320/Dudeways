package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityBankDetailsBinding
import com.gmwapp.dudeways.databinding.ActivityCustomerSupportBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONException
import org.json.JSONObject

class BankDetailsActivity : BaseActivity() {

    lateinit var binding: ActivityBankDetailsBinding
    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBankDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        binding.etHolderName.text = Editable.Factory.getInstance().newEditable(session.getData(Constant.ACCOUNT_HOLDER_NAME))
        binding.etAccountNumber.text = Editable.Factory.getInstance().newEditable(session.getData(Constant.ACCOUNT_NUMBER))
        binding.etIfsccode.text = Editable.Factory.getInstance().newEditable(session.getData(Constant.IFSC_CODE))
        binding.etBankName.text = Editable.Factory.getInstance().newEditable(session.getData(Constant.BANK_NAME))
        binding.etBranchName.text = Editable.Factory.getInstance().newEditable(session.getData(Constant.BRANCH_NAME))

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnUpdate.setOnClickListener {
            if (binding.etHolderName.text.toString().isEmpty()) {
                binding.etHolderName.error = "Please enter Holder Name"
                return@setOnClickListener
            } else if (binding.etAccountNumber.text.toString().isEmpty()) {
                binding.etAccountNumber.error = "Please enter Account Number"
                return@setOnClickListener
            } else if (binding.etIfsccode.text.toString().isEmpty()) {
                binding.etIfsccode.error = "Please enter IFCS code"
                return@setOnClickListener
            } else if (binding.etBankName.text.toString().isEmpty()) {
                binding.etBankName.error = "Please enter Bank Name"
                return@setOnClickListener
            }else if (binding.etBranchName.text.toString().isEmpty()) {
                binding.etBranchName.error = "Please enter Branch Name"
                return@setOnClickListener
            } else {
                updateBank()
            }
        }

    }

    private fun updateBank() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.ACCOUNT_HOLDER_NAME] = binding.etHolderName.text.toString()
        params[Constant.ACCOUNT_NUMBER] = binding.etAccountNumber.text.toString()
        params[Constant.IFSC_CODE] = binding.etIfsccode.text.toString()
        params[Constant.BANK_NAME] = binding.etBankName.text.toString()
        params[Constant.BRANCH_NAME] = binding.etBranchName.text.toString()

        // API call to update bank details
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                        // Update session data
                        session.setData(Constant.ACCOUNT_HOLDER_NAME, binding.etHolderName.text.toString())
                        session.setData(Constant.ACCOUNT_NUMBER, binding.etAccountNumber.text.toString())
                        session.setData(Constant.IFSC_CODE, binding.etIfsccode.text.toString())
                        session.setData(Constant.BANK_NAME, binding.etBankName.text.toString())
                        session.setData(Constant.BRANCH_NAME, binding.etBranchName.text.toString())

                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("API_ERROR", "Error parsing response", e)
                    Toast.makeText(activity, "Error parsing response", Toast.LENGTH_SHORT).show()
                }
            }
        }, activity, Constant.UPDATE_BANK, params, true, 1)

        // Debugging logs
        Log.d("UPDATE_BANK", "UPDATE_BANK URL: ${Constant.UPDATE_BANK}")
        Log.d("UPDATE_BANK", "UPDATE_BANK Params: $params")
    }

}