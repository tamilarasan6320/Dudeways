package com.gmwapp.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityProfileinfoBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject

class ProfileinfoActivity : BaseActivity() {
    lateinit var binding: ActivityProfileinfoBinding
    lateinit var activity: Activity
    lateinit var session: Session
    var user_id: String? = null
    var unique_name: String? = null
    var name: String? = null
    var profile: String? = null
    var friend_verified: String? = null
    var friend: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profileinfo)
        binding = ActivityProfileinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener{
            onBackPressed()
        }



        activity = this
        session = Session(activity)



        val civProfile: CircleImageView = findViewById(R.id.civProfile)
        val fullscreenContainer: View = findViewById(R.id.fullscreen_container)
        val fullscreenImage: CircleImageView = findViewById(R.id.fullscreen_image)

        civProfile.setOnClickListener {
            // Set the image resource to the fullscreen image view
            fullscreenImage.setImageDrawable(civProfile.drawable)

            // Show the fullscreen container
            fullscreenContainer.visibility = View.VISIBLE

            // Load and apply the zoom animation
            val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
            fullscreenImage.startAnimation(zoomInAnimation)
        }

        // Hide the fullscreen image on click
        fullscreenContainer.setOnClickListener {
            // Load and apply the zoom out animation
            val zoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
            fullscreenImage.startAnimation(zoomOutAnimation)

            // Set a listener to hide the container after the animation ends
            zoomOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    fullscreenContainer.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }


        user_id = intent.getStringExtra("chat_user_id")
        userdetails(user_id)




        val id = intent.getStringExtra("id")






        binding.rlAddFriend.setOnClickListener {
            // Change the background of rlAddFriend
            if (friend == "0") {
                val friends = "1"
                friend= "1"
                add_freind(binding.ivaddFriend, binding.tvAddFriend, user_id,friends)
            } else if (friend == "1"   ) {
                val friends = "2"
                friend = "0"
                add_freind(binding.ivaddFriend, binding.tvAddFriend, user_id,friends)
            }




        }

        binding.rlChat.setOnClickListener {
            if (user_id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("name", name)
                session.setData("reciver_profile", profile )
                intent.putExtra("chat_user_id", user_id)
                intent.putExtra("unique_name",unique_name )
                intent.putExtra("friend_verified", friend_verified)
                activity.startActivity(intent)
            }
        }




        profile_view(user_id)


    }


    private fun userdetails(user_id: String?) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.OTHER_USER_ID] = user_id!!
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val `object` = JSONObject(response)
                        val jsonobj = `object`.getJSONObject(Constant.DATA)


                        Glide.with(activity).load(jsonobj.getString(Constant.COVER_IMG)).placeholder(R.drawable.placeholder_bg).into(binding.ivCover)
                        Glide.with(activity).load(jsonobj.getString(Constant.PROFILE)).placeholder(R.drawable.profile_placeholder).into(binding.civProfile)



                        val gender = jsonobj.getString(Constant.GENDER)
                        val age = jsonobj.getString(Constant.AGE)
                        val verify = jsonobj.getString("verified")

                        if (verify == "1") {
                            binding.ivVerify.visibility = View.VISIBLE
                        } else {
                            binding.ivVerify.visibility = View.GONE
                        }

                        binding.ivAge.text = age

                        if(gender == "male") {
                            binding.ivGender.setBackgroundDrawable(resources.getDrawable(R.drawable.male_ic))
                        }
                        else {
                            binding.ivGender.setBackgroundDrawable(resources.getDrawable(R.drawable.female_ic))
                        }

                        if (gender == "male") {
                            binding.ivGenderColor.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(this, R.color.blue_200))
                        } else {
                            binding.ivGenderColor.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(this, R.color.primary))
                        }


                        binding.tvName.text = jsonobj.getString(Constant.NAME)
                        binding.tvProfessional.text =  jsonobj.getString(Constant.PROFESSION)
                        binding.tvCity.text = jsonobj.getString(Constant.CITY)
                        binding.tvState.text = jsonobj.getString(Constant.STATE)
                        binding.tvGender.text = jsonobj.getString(Constant.GENDER)
                        binding.tvUsername.text = "@"+jsonobj.getString(Constant.UNIQUE_NAME)
                        binding.tvPlace.text = jsonobj.getString(Constant.CITY) + ", " + jsonobj.getString(Constant.STATE)


                        unique_name = jsonobj.getString(Constant.UNIQUE_NAME)
                        name = jsonobj.getString(Constant.NAME)
                        profile = jsonobj.getString(Constant.PROFILE)
                        friend_verified = jsonobj.getString(Constant.VERIFIED)
                        friend = jsonobj.getString("friend_status")


                        var friend_data = "" + friend

                     //   Toast.makeText(activity, friend_data, Toast.LENGTH_SHORT).show()

                        if (friend_data == "0") {
                            binding.ivaddFriend.setBackgroundResource(R.drawable.add_account)
                            binding.tvAddFriend.text = "Add to Friend"
                        } else if (friend_data == "1") {
                            binding.ivaddFriend.setBackgroundResource(R.drawable.added_frd)
                            binding.tvAddFriend.text = "Friend Added"
                        }


                        // Toast.makeText(activity, friend, Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.OTHER_USER_DETAILS, params, true, 1)
    }


    private fun add_freind(ivaddFriend: ImageView, tvAddFriend: TextView, user_id: String?, friend: String) {
        val session = Session(activity)
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.FRIEND_USER_ID] = user_id!!
        params[Constant.FRIEND] = friend

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {


                        if (friend == "1") {
                            ivaddFriend.setBackgroundResource(R.drawable.added_frd)
                            tvAddFriend.text = "Friend Added"

                        } else if (friend == "2") {
                            ivaddFriend.setBackgroundResource(R.drawable.add_account)
                            tvAddFriend.text = "Add to Friend"
                        }

                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()


                    } else {
                       Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            // Stop the refreshing animation once the network request is complete

        }, activity, Constant.ADD_FRIENDS, params, true, 1)
    }
    private fun profile_view(user_id: String?) {
        val session = Session(activity)
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.PROFILE_USER_ID] = user_id!!
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {



                     //   Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()


                    } else {
                       // Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            // Stop the refreshing animation once the network request is complete

        }, activity, Constant.PROFILE_VIEW, params, true, 1)
    }
}