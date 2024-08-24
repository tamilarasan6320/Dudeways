package com.gmwapp.dudeways.helper

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException

class ApiConfig : Application() {
    private var mRequestQueue: RequestQueue? = null

    companion object {
        @JvmStatic
        lateinit var mInstance: ApiConfig
        lateinit var session: Session
        const val TAG: String = "ApiConfig"

        @JvmStatic
        fun getInstance(): ApiConfig {
            return mInstance
        }

        @JvmStatic
        fun isConnected(activity: Activity): Boolean {
            return try {
                val connectionManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectionManager.activeNetworkInfo
                networkInfo != null && networkInfo.isConnected
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        @JvmStatic
        fun volleyErrorMessage(error: VolleyError): String {
            return when (error) {
                is NetworkError -> "Cannot connect to Internet...Please check your connection!"
                is ServerError -> "The server could not be found. Please try again after some time!"
                is AuthFailureError -> "Cannot connect to Internet...Please check your connection!"
                is ParseError -> "Parsing error! Please try again after some time!"
                is TimeoutError -> "Connection TimeOut! Please check your internet connection."
                else -> ""
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @JvmStatic
        fun RequestToVolley(callback: VolleyCallback, activity: Activity, url: String, params: Map<String, String>, isProgress: Boolean, method: Int = Request.Method.POST) {
            session = Session(activity)
            ProgressDisplay.mProgressBar?.visibility = View.GONE
            val progressDisplay = ProgressDisplay(activity)
            progressDisplay.hideProgress()

            if (isProgress) progressDisplay.showProgress()
            val stringRequest = object : StringRequest(method, url, Response.Listener { response ->
                if (isConnected(activity)) callback.onSuccess(true, response)
                if (isProgress) progressDisplay.hideProgress()
            }, Response.ErrorListener { error ->
                try {
                    if (error.networkResponse != null) {
                        val responseBody = String(error.networkResponse.data, Charsets.UTF_8)
                        val jsonObject = JSONObject(responseBody)
                        if (isConnected(activity)) callback.onSuccess(true, responseBody)
                    } else {
                        if (isConnected(activity)) callback.onSuccess(false, "")
                        val message = volleyErrorMessage(error)
                        if (message.isNotEmpty()) Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    }
                    if (isProgress) progressDisplay.hideProgress()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (error: UnsupportedEncodingException) {
                    error.printStackTrace()
                }
            }) {
                override fun getHeaders(): Map<String, String> {
                    val params1 = HashMap<String, String>()
                    params1[Constant.AUTHORIZATION] = "Bearer " + session.getData(Constant.TOKEN)
                    return params1
                }

                override fun getParams(): Map<String, String> {
                    return params
                }
            }

            stringRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            getInstance().requestQueue.cache.clear()
            getInstance().addToRequestQueue(stringRequest)
        }

        @JvmStatic
        fun requestToVolley(callback: VolleyCallback, activity: Activity, url: String, params: Map<String, String>, isProgress: Boolean) {
            ProgressDisplay.mProgressBar?.visibility = View.GONE
            val progressDisplay = ProgressDisplay(activity)
            progressDisplay.hideProgress()

            if (isProgress) progressDisplay.showProgress()
            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                if (isConnected(activity)) callback.onSuccess(true, response)
                if (isProgress) progressDisplay.hideProgress()
            }, Response.ErrorListener { error ->
                if (isProgress) progressDisplay.hideProgress()
                if (isConnected(activity)) callback.onSuccess(false, "")
                val message = volleyErrorMessage(error)
                if (message.isNotEmpty()) Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }) {
                override fun getParams(): Map<String, String> {
                    return params
                }
            }

            stringRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            getInstance().requestQueue.cache.clear()
            getInstance().addToRequestQueue(stringRequest)
        }

        @JvmStatic
        fun requestToVolleyMulti(callback: VolleyCallback, activity: Activity, url: String, params: Map<String, String>, fileParams: Map<String, String>) {
            if (isConnected(activity)) {
                val multipartRequest = object : VolleyMultiPartRequest(url, Response.Listener { response ->
                    callback.onSuccess(true, response)
                }, Response.ErrorListener { error ->
                    callback.onSuccess(false, "")
                }) {
                    override fun getDefaultParams(): Map<String, String> {
                        return params
                    }

                    override fun getFileParams(): Map<String, String> {
                        return fileParams
                    }
                }

                multipartRequest.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                getInstance().requestQueue.cache.clear()
                getInstance().addToRequestQueue(multipartRequest)
            }
        }

        @JvmStatic
        fun getVolleyRequest(callback: VolleyCallback, activity: Activity, url: String, params: Map<String, String>, isProgress: Boolean) {
            ProgressDisplay.mProgressBar?.visibility = View.GONE
            val progressDisplay = ProgressDisplay(activity)
            progressDisplay.hideProgress()

            if (isProgress) progressDisplay.showProgress()
            val stringRequest = object : StringRequest(Request.Method.GET, url, Response.Listener { response ->
                if (isConnected(activity)) callback.onSuccess(true, response)
                if (isProgress) progressDisplay.hideProgress()
            }, Response.ErrorListener { error ->
                if (isProgress) progressDisplay.hideProgress()
                if (isConnected(activity)) callback.onSuccess(false, "")
                val message = volleyErrorMessage(error)
                if (message.isNotEmpty()) Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }) {
                override fun getParams(): Map<String, String> {
                    return params
                }
            }

            stringRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            getInstance().requestQueue.cache.clear()
            getInstance().addToRequestQueue(stringRequest)
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(applicationContext)
            }
            return mRequestQueue!!
        }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        requestQueue.add(req)
    }
}
