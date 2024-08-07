package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityFreePointsBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import org.json.JSONException
import org.json.JSONObject

class FreePointsActivity : BaseActivity() {

    private lateinit var binding: ActivityFreePointsBinding
    private lateinit var activity: Activity
    private lateinit var session: Session
    private var rewardedAd: RewardedAd? = null
    private val adId = "ca-app-pub-8693482193769963/5956761344"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_points)
        binding = ActivityFreePointsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)
        MobileAds.initialize(this) {}

        loadRewardedVideoAd()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.llStep1.setOnClickListener {
            val proof1 = session.getData(Constant.SELFIE_IMAGE)
            val proof2 = session.getData(Constant.FRONT_IMAGE)
            val proof3 = session.getData(Constant.BACK_IMAGE)
            val status = session.getData(Constant.STATUS)
            val payment_status = session.getData(Constant.PAYMENT_STATUS)


            // if proof 1 2 3 is empty
            if(proof1.isEmpty() || proof2.isEmpty() || proof3.isEmpty()) {
                val intent = Intent(activity, IdverficationActivity::class.java)
                startActivity(intent)
            }
            else if (payment_status == "0") {
                val intent = Intent(activity, PurchaseverifybuttonActivity::class.java)
                startActivity(intent)
            }
            else if (status == "0") {
                val intent = Intent(activity, Stage4Activity::class.java)
                startActivity(intent)
            }

            else if (status == "1"){
                val intent = Intent(activity, VerifiedActivity::class.java)
                startActivity(intent)
            }


        }

        binding.llStep2.setOnClickListener {
            val intent = Intent(activity, spinActivity::class.java)
            startActivity(intent)
        }

        binding.llStep4.setOnClickListener {
            val intent = Intent(activity, InviteFriendsActivity::class.java)
            startActivity(intent)
        }

        binding.llStep3.setOnClickListener {
            // Check if the ad is loaded before allowing the user to watch it
            if (rewardedAd != null) {
                showRewardedVideoAd()
            } else {
                loadRewardedVideoAd()
                showRewardedVideoAd()
            }
        }
    }

    private fun loadRewardedVideoAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, adId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                rewardedAd = null
              //  Toast.makeText(this@FreePointsActivity, "Ad Failed To Load", Toast.LENGTH_SHORT).show()
            }

            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
              //  Toast.makeText(this@FreePointsActivity, "Ad Loaded", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRewardedVideoAd() {
        rewardedAd?.let { ad ->
            ad.show(this) { rewardItem: RewardItem ->
                addPurchase()
                Toast.makeText(this, "Video Completed", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            loadRewardedVideoAd()
        }
    }

    private fun addPurchase() {
        val points = "1"

        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params["points"] = points

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonobj = jsonObject.getJSONObject(Constant.DATA)
                        session.setData(Constant.POINTS, jsonobj.getString(Constant.POINTS))
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.REWARD_POINTS, params, true, 1)
    }
}
