package com.gmwapp.dudeways.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.gmwapp.dudeways.Fragment.ExploreFragment
import com.gmwapp.dudeways.Fragment.HomeFragment
import com.gmwapp.dudeways.Fragment.InterestFragment
import com.gmwapp.dudeways.Fragment.MessagesFragment
import com.gmwapp.dudeways.Fragment.NotificationFragment
import com.gmwapp.dudeways.Fragment.TripFragment
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityHomeBinding
import com.gmwapp.dudeways.helper.ApiConfig
import com.gmwapp.dudeways.helper.Constant
import com.gmwapp.dudeways.helper.Session
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.Fragment.MyProfileFragment
import com.gmwapp.dudeways.Fragment.SearchFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.zoho.commons.InitConfig
import com.zoho.livechat.android.listeners.InitListener
import com.zoho.salesiqembed.ZohoSalesIQ
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class HomeActivity : BaseActivity() , NavigationBarView.OnItemSelectedListener {

    lateinit var binding: ActivityHomeBinding
    lateinit var activity: Activity
    lateinit var session: Session

    private lateinit var fm: FragmentManager

    val ONESIGNAL_APP_ID = "4f929ed9-584d-4208-a3e8-7de1ae4f679e"

    private var bottomNavigationView: BottomNavigationView? = null

    private var tripFragment = TripFragment()
    private var exploreFragment = ExploreFragment()
    private var messagesFragment = MessagesFragment()
    private var homeFragment = HomeFragment()
    private var searchFragment = SearchFragment()
    private var notification = NotificationFragment()
    private var myProfileFragment = MyProfileFragment()
    private var interestFragment = InterestFragment()

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeComponents()
        initializeOneSignal()
        initializeZohoSalesIQ()
        setupBottomNavigation()
        loadProfilePicture()



        // Restore selected item from saved instance state if available
        if (savedInstanceState != null) {
            val selectedItemId = savedInstanceState.getInt("selectedItemId", R.id.navHome)
            bottomNavigationView?.selectedItemId = selectedItemId
        }









    }

    private fun chatBadge() {
       // Toast.makeText(activity, session.getData(Constant.UNREAD_COUNT.toString()), Toast.LENGTH_SHORT).show()
        val chatBadge = bottomNavigationView!!.getOrCreateBadge(R.id.navMessages)
        chatBadge.number = session.getData(Constant.UNREAD_COUNT.toString()).toInt()

        if (chatBadge.number == 0) {
            chatBadge.isVisible = false
        }
        else{
            chatBadge.isVisible = true
        }


        chatBadge.backgroundColor = ContextCompat.getColor(this, R.color.primary)
        chatBadge.badgeTextColor = ContextCompat.getColor(this, R.color.white)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the selected item id
        outState.putInt("selectedItemId", bottomNavigationView?.selectedItemId ?: R.id.navHome)
    }

    private fun initializeComponents() {
        activity = this
        session = Session(activity)
        fm = supportFragmentManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tripFragment = TripFragment()
        exploreFragment = ExploreFragment()
        messagesFragment = MessagesFragment()
        homeFragment = HomeFragment()
        notification = NotificationFragment()
        interestFragment = InterestFragment()
        myProfileFragment = MyProfileFragment()
        searchFragment = SearchFragment()

        fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()


        binding.ivSearch.setOnClickListener {
            fm.beginTransaction().replace(R.id.fragment_container, searchFragment).commit()
        }

    }

    private fun initializeOneSignal() {
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }
        OneSignal.login("${session.getData(Constant.USER_ID)}")
    }

    private fun initializeZohoSalesIQ() {
        val initConfig = InitConfig()
        ZohoSalesIQ.init(application, "FkbMlSXKPaATeaZN35ZmjRdYaU29Wkx2QMkU75bCptU3ZA8TYZl2%2B%2BvFc55TAwVG_in", "xHGPBNAi6lC%2Fm7ngAEy%2FPSgch2eW42oPrw91hcyRHElKYqtGLQkB%2FuE%2F7QPdaD9BbNzYtKPn9U0kV316gEW6vUjMNMCKY5Jey7HFiemj%2BueB02iLgVZl6g%3D%3D", initConfig, object :
            InitListener {
            override fun onInitSuccess() {
                // Initialization successful
            }

            override fun onInitError(errorCode: Int, errorMessage: String) {
                // Handle initialization errors
            }
        })
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView!!.setOnItemSelectedListener(this)
        bottomNavigationView!!.selectedItemId = R.id.navHome
    }

    private fun loadProfilePicture() {
        val profile = session.getData(Constant.PROFILE)
        Glide.with(activity).load(profile).placeholder(R.drawable.profile_placeholder)
            .into(binding.civProfile)

        binding.civProfile.setOnClickListener {
//            val intent = Intent(activity, ProfileViewActivity::class.java)
//            startActivity(intent)
            fm.beginTransaction().replace(R.id.fragment_container, myProfileFragment).commit()


        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val transaction = fm.beginTransaction()
        when (item.itemId) {
            R.id.navHome -> {
                transaction.replace(R.id.fragment_container, homeFragment)
                onStart()
            }
            R.id.navExplore -> {
                transaction.replace(R.id.fragment_container, tripFragment)
               onStart()
            }
            R.id.navIntersts -> {
                transaction.replace(R.id.fragment_container, interestFragment)
                onStart()
            }
            R.id.navMessages -> {
                transaction.replace(R.id.fragment_container, messagesFragment)
                onStart()
            }
            R.id.navNotification -> {
                transaction.replace(R.id.fragment_container, notification)
                onStart()
            }
           /* R.id.navProfile -> {
                transaction.replace(R.id.fragment_container, myProfileFragment)
                onStart()
            }*/
        }
        transaction.commit()
        setting()
        return true
    }


    override fun onBackPressed() {
        val currentFragment = fm.findFragmentById(R.id.fragment_container)

        if (currentFragment is SearchFragment) {
            // If current fragment is SearchFragment, replace it with HomeFragment
            fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()
        } else if (currentFragment is MyProfileFragment){
            fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()

        }
        else {
            // If not, follow the default behavior
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel()
                super.onBackPressed()
                return
            } else {
                backToast = Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT)
                backToast.show()
            }
            backPressedTime = System.currentTimeMillis()
        }
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        var addresses: List<Address>? = null
                        var retryCount = 3
                        while (retryCount > 0) {
                            try {
                                addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                if (addresses != null && addresses.isNotEmpty()) {
                                    break
                                }
                            } catch (e: IOException) {
                                Log.e("GeocoderError", "Service not Available. Retries left: $retryCount", e)
                                retryCount--
                                if (retryCount == 0) {
                                    Log.e("GeocoderError", "Failed to get location after retries", e)
                                    Toast.makeText(this, "Failed to get location. Please try again later.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        if (addresses != null && addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val latitude = address.latitude
                            val longitude = address.longitude
                            location(latitude, longitude)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun location(latitude: Double?, longitude: Double?) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.LATITUDE] = latitude.toString()
        params[Constant.LONGITUDE] = longitude.toString()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                       // userdetails(session.getData(Constant.USER_ID),"1")
                        // Location updated successfully
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.UPDATE_LOCATION, params, true, 1)
    }

    private fun setting() {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject: JSONObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {


                        val array = jsonObject.getJSONArray("data")

                        session.setData(Constant.INSTAGRAM_LINK, array.getJSONObject(0).getString(Constant.INSTAGRAM_LINK))
                        session.setData(Constant.TELEGRAM_LINK, array.getJSONObject(0).getString(Constant.TELEGRAM_LINK))
                        session.setData(Constant.UPI_ID, array.getJSONObject(0).getString(Constant.UPI_ID))


                    } else {
                        Toast.makeText(
                            activity,
                            jsonObject.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(activity, response, Toast.LENGTH_SHORT).show()
            }

        }, activity, Constant.SETTINGS_LIST, params, true, 1)
    }


    private fun userdetails(user_id: String?, status: String?) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = user_id.toString()
        params[Constant.ONLINE_STATUS] = status.toString()
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
                        session.setData(Constant.GENDER, jsonobj.getString (Constant.GENDER))
                        session.setData(Constant.PROFESSION, jsonobj.getString(Constant.PROFESSION))
                        session.setData(Constant.STATE, jsonobj.getString(Constant.STATE))
                        session.setData(Constant.CITY, jsonobj.getString(Constant.CITY))
                        session.setData(Constant.PROFILE, jsonobj.getString(Constant.PROFILE))
                        session.setData(Constant.MOBILE, jsonobj.getString(Constant.MOBILE))
                        session.setData(Constant.REFER_CODE, jsonobj.getString(Constant.REFER_CODE))
                        session.setData(Constant.COVER_IMG, jsonobj.getString(Constant.COVER_IMG))
                        session.setData(Constant.POINTS, jsonobj.getString(Constant.POINTS))
                        session.setData(Constant.VERIFIED, jsonobj.getString(Constant.VERIFIED))
                        session.setData(Constant.ONLINE_STATUS, jsonobj.getString(Constant.ONLINE_STATUS))
                        session.setData(Constant.INTRODUCTION, jsonobj.getString(Constant.INTRODUCTION))
                        session.setData(Constant.MESSAGE_NOTIFY, jsonobj.getString(Constant.MESSAGE_NOTIFY))
                        session.setData(Constant.ADD_FRIEND_NOTIFY, jsonobj.getString(Constant.ADD_FRIEND_NOTIFY))
                        session.setData(Constant.VIEW_NOTIFY, jsonobj.getString(Constant.VIEW_NOTIFY))
                        session.setData(Constant.PROFILE_VERIFIED, jsonobj.getString(Constant.PROFILE_VERIFIED))
                        session.setData(Constant.CHAT_STATUS, jsonobj.getString(Constant.CHAT_STATUS))
                        session.setData(Constant.UNREAD_COUNT, jsonobj.getString(Constant.UNREAD_COUNT))

                        val latitude = jsonobj.getString(Constant.LATITUDE)
                        val longitude = jsonobj.getString(Constant.LONGITUDE)
                        session.setData(Constant.LATITUDE, latitude)
                        session.setData(Constant.LONGITUDE, longitude)

                        if (latitude == "" && longitude == "") {
                            getLocation()
                        }


                        chatBadge()

                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.USERDETAILS, params, false, 1)
    }

    // onstart
    override fun onStart() {
        super.onStart()
        userdetails(session.getData(Constant.USER_ID),"1")

    }

    // onstop
    override fun onStop() {
        super.onStop()
        userdetails(session.getData(Constant.USER_ID),"0")
    }

    override fun onResume() {
        super.onResume()
        onStart()
    }



}
