package com.app.dudeways.Activity

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityProfileinfoBinding
import com.app.dudeways.helper.ApiConfig
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import org.json.JSONException
import org.json.JSONObject

class ProfileinfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileinfoBinding
    lateinit var activity: Activity
    lateinit var session: Session
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
        val user_id = intent.getStringExtra("chat_user_id")
        val id = intent.getStringExtra("id")
        val friend = intent.getStringExtra("friend")
        val name = intent.getStringExtra("name")
        val profile = intent.getStringExtra("profile")


         var friend_data = "" + friend

        if (friend_data == "0") {
            binding.ivaddFriend.setBackgroundResource(R.drawable.add_account)
            binding.tvAddFriend.text = "Add to Friend"
        } else if (friend_data == "1") {
            binding.ivaddFriend.setBackgroundResource(R.drawable.added_frd)
            binding.tvAddFriend.text = "Friend Added"
        }


        binding.rlAddFriend.setOnClickListener {
            // Change the background of rlAddFriend
            if (friend_data == "0") {
                val friend = "1"
                friend_data = "1"
                add_freind(binding.ivaddFriend, binding.tvAddFriend, id,friend)
            } else if (friend_data == "1"   ) {
                val friend = "2"
                friend_data = "0"
                add_freind(binding.ivaddFriend, binding.tvAddFriend, id,friend)
            }




        }

        binding.rlChat.setOnClickListener {
            val intent = Intent(activity, ChatsActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("name", name)
            session.setData("reciver_profile", profile )
            intent.putExtra("chat_user_id", user_id)
            activity.startActivity(intent)
        }


        userdetails(user_id)


    }


    private fun userdetails(user_id: String?) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = user_id.toString()
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





                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.OTHER_USER_DETAILS, params, true, 1)
    }


    private fun add_freind(ivaddFriend: ImageView, tvAddFriend: TextView, id: String?, friend: String) {
        val session = Session(activity)
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.FRIEND_USER_ID] = id!!
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

            // Stop the refreshing animation once the network request is complete

        }, activity, Constant.ADD_FRIENDS, params, true, 1)
    }
}