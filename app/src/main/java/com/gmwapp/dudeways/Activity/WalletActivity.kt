package com.gmwapp.dudeways.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmwapp.dudeways.Adapter.WithdrawalAdapter
import com.gmwapp.dudeways.Model.WithdrawalList
import com.gmwapp.dudeways.databinding.ActivityWalletBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject

class WalletActivity : BaseActivity() {

    private lateinit var binding: ActivityWalletBinding
    lateinit var activity: Activity
    lateinit var session: Session

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        session = Session(activity)

        // Load withdrawal list initially
        withdrawalList()

        binding.tvBalanceAmount.text = "₹" + session.getData(Constant.BALANCE)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvAddBank.setOnClickListener {
            val intent = android.content.Intent(activity, BankDetailsActivity::class.java)
            startActivity(intent)
        }

        binding.btnWithdraw.setOnClickListener {
            if (binding.etAmount.text.toString().isEmpty()) {
                binding.etAmount.error = "Please enter Amount"
                return@setOnClickListener
            } else {
                withdrawal()
            }
        }
    }

    private fun withdrawal() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.AMOUNT] = binding.etAmount.text.toString()

        // API call for withdrawal action
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        // If successful, refresh withdrawal list
                        withdrawalList()

                        binding.tvBalanceAmount.text = "₹" + jsonObject.getString(Constant.BALANCE)

                        Toast.makeText(
                            activity,
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()

                        // Reload the activity after successful withdrawal
                        val intent = intent
                        finish() // Finish the current activity
                        startActivity(intent) // Start the activity again

                    } else if (jsonObject.getString(Constant.MESSAGE) != "No withdrawals found for this user.") {
                        Toast.makeText(
                            activity,
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.WITHDRAWALS, params, true, 1)
    }

    private fun withdrawalList() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)

        // API call to get the list of withdrawals
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val dataArray = jsonObject.getJSONArray("data")
                        val withdrawalList = ArrayList<WithdrawalList>()

                        for (i in 0 until dataArray.length()) {
                            val item = dataArray.getJSONObject(i)
                            val withdrawal = WithdrawalList(
                                id = item.getInt("id"),
                                userId = item.getString("user_id"),
                                amount = item.getString("amount"),
                                datetime = item.getString("datetime"),
                                status = item.getInt("status")
                            )
                            withdrawalList.add(withdrawal)
                        }

                        // Set up the RecyclerView
                        val adapter = WithdrawalAdapter(activity, withdrawalList) // Pass both the activity and the list
                        binding.rvWithdrawStatus.adapter = adapter
                        binding.rvWithdrawStatus.layoutManager = LinearLayoutManager(activity)

                    } else {
                        Toast.makeText(
                            activity,
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.WITHDRAWALS_LIST, params, true, 1)
    }
}

