package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityFreePointsBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import org.json.JSONException
import org.json.JSONObject


class FreePointsActivity : AppCompatActivity() {

    lateinit var binding: ActivityFreePointsBinding
    lateinit var activity: Activity
    lateinit var session: Session
    private lateinit var adMobRewardedVideoAd: RewardedVideoAd
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_points)
        binding = ActivityFreePointsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)
        MobileAds.initialize(this)
        adMobRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)

        loadRewardedVideoAd()

//        binding.progressBar.indeterminateDrawable.setColorFilter(
//            ContextCompat.getColor(this, R.color.primary),
//            PorterDuff.Mode.SRC_IN
//        )


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        binding.llStep1.setOnClickListener {
            val proof1 = session.getData(Constant.PROOF1)
            val proof2 = session.getData(Constant.PROOF2)

            if(proof1 == "1" && proof2 == "1") {
                val intent = Intent(activity, Stage4Activity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(activity, IdverficationActivity::class.java)
                startActivity(intent)
            }
        }

        binding.llStep2.setOnClickListener {
            val intent = Intent(activity, spinActivity::class.java)
            startActivity(intent)
        }

        // Set OnClickListener for Step 3 button
        binding.llStep3.setOnClickListener {
            // Check if the ad is loaded before allowing the user to watch it
            if (adMobRewardedVideoAd.isLoaded) {
                showRewardedVideoAd()
            } else {
                loadRewardedVideoAd()
                showRewardedVideoAd()
            }
        }
    }

    private val adId = "ca-app-pub-3940256099942544/5224354917"

    private fun loadRewardedVideoAd() {
        adMobRewardedVideoAd.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoAdLoaded() {
              //  binding.progressBar.visibility = View.GONE
             //   Toast.makeText(this@FreePointsActivity, "Ad Loaded", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedVideoAdOpened() {
              //  Toast.makeText(this@FreePointsActivity, "Ad Opened", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedVideoStarted() {
               // Toast.makeText(this@FreePointsActivity, "Video Started", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedVideoAdClosed() {
            //    Toast.makeText(this@FreePointsActivity, "Ad Closed", Toast.LENGTH_SHORT).show()
            }

            override fun onRewarded(p0: com.google.android.gms.ads.reward.RewardItem?) {
                TODO("Not yet implemented")
            }


            override fun onRewardedVideoAdLeftApplication() {
              //  Toast.makeText(this@FreePointsActivity, "Left Application", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedVideoAdFailedToLoad(i: Int) {
             //   Toast.makeText(this@FreePointsActivity, "Ad Failed To Load", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedVideoCompleted() {
                addpurchase()
              //  Toast.makeText(this@FreePointsActivity, "Video Completed", Toast.LENGTH_SHORT).show()
            }
        }

        adMobRewardedVideoAd.loadAd(adId, AdRequest.Builder().build())
    }

    private fun showRewardedVideoAd() {
        if (adMobRewardedVideoAd.isLoaded) {
            adMobRewardedVideoAd.show()
        } else {
            adMobRewardedVideoAd.loadAd(adId, AdRequest.Builder().build())
        }
    }


    private fun addpurchase() {

        var points = "1"

        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        //  params["points_id"] = id
        params["points"] = points
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)


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