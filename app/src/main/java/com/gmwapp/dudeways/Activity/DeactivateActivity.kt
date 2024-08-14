package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityDeactivateBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.android.material.card.MaterialCardView
import org.json.JSONException
import org.json.JSONObject

class DeactivateActivity : BaseActivity() {


    lateinit var binding: ActivityDeactivateBinding
    lateinit var activity: Activity
    lateinit var session: Session

    private lateinit var cardEmoji1: MaterialCardView
    private lateinit var cardEmoji2: MaterialCardView
    private lateinit var cardEmoji3: MaterialCardView
    private lateinit var cardEmoji4: MaterialCardView
    private lateinit var cardEmoji5: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deactivate)

        binding = ActivityDeactivateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        cardEmoji1 = findViewById(R.id.cardEmoji1)
        cardEmoji2 = findViewById(R.id.cardEmoji2)
        cardEmoji3 = findViewById(R.id.cardEmoji3)
        cardEmoji4 = findViewById(R.id.cardEmoji4)
        cardEmoji5 = findViewById(R.id.cardEmoji5)

        val cards = listOf(cardEmoji1, cardEmoji2, cardEmoji3, cardEmoji4, cardEmoji5)

        cards.forEach { card ->
            card.setOnClickListener {
                selectCard(card, cards)
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnfeedback.setOnClickListener {

            if (binding.etFeedback.text.toString().isEmpty()) {
                binding.etFeedback.error = "Please enter feedback"
                return@setOnClickListener
            }else{
                apicall()
            }

        }


    }

    private fun selectCard(selectedCard: MaterialCardView, cards: List<MaterialCardView>) {
        val defaultColor = ContextCompat.getColor(this, R.color.white)
        val selectedColor = ContextCompat.getColor(this, R.color.feedback_select)

        cards.forEach { it.setCardBackgroundColor(defaultColor) }

        selectedCard.setCardBackgroundColor(selectedColor)

        when (selectedCard.id) {
            R.id.cardEmoji1 -> {

            }
            R.id.cardEmoji2 -> {

            }
            R.id.cardEmoji3 -> {

            }
            R.id.cardEmoji4 -> {

            }
            R.id.cardEmoji5 -> {

            }
        }
    }

    private fun apicall() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params["feedback"] = binding.etFeedback.text.toString()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            // Stop the refreshing animation once the network request is complete

        }, activity, Constant.ADD_FEEDBACK, params, true, 1)

    }
}