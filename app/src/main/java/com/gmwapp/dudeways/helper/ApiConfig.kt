package com.gmwapp.dudeways.helper

import android.app.Activity
import android.app.Application
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkError
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException

class ApiConfig : Application() {
    var mRequestQueue: RequestQueue? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(applicationContext)
            }
            return mRequestQueue as RequestQueue
        }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.setTag(TAG)
        requestQueue.add(req)
    }

    companion object {
        @get:Synchronized
        var instance: ApiConfig? = null
        val TAG: String = ApiConfig::class.java.simpleName
        var session: Session? = null

        fun VolleyErrorMessage(error: VolleyError?): String {
            var message = ""
            try {
                if (error is NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!"
                } else if (error is ServerError) {
                    message = "The server could not be found. Please try again after some time!"
                } else if (error is AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!"
                } else if (error is ParseError) {
                    message = "Parsing error! Please try again after some time!"
                } else if (error is TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection."
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return message
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        fun RequestToVolley(
            callback: VolleyCallback,
            activity: Activity,
            url: String?,
            params: Map<String, String>?,
            isProgress: Boolean,
            method: Int
        ) {
            session = Session(activity)
            if (ProgressDisplay.mProgressBar != null) {
                ProgressDisplay.mProgressBar.visibility = View.GONE
            }
            val progressDisplay = ProgressDisplay(activity)
            progressDisplay.hideProgress()

            if (isProgress) progressDisplay.showProgress()
            val stringRequest: StringRequest =
                object : StringRequest(method, url, Response.Listener { s ->
                    if (isConnected(activity)) callback.onSuccess(true, s)
                    if (isProgress) progressDisplay.hideProgress()
                }, Response.ErrorListener { volleyError ->
                    try {
                        if (volleyError.networkResponse != null) {
                            val responseBody =
                                String(volleyError.networkResponse.data, charset("utf-8"))
                            val jsonObject = JSONObject(responseBody)
                            if (isConnected(activity)) callback.onSuccess(true, responseBody)
                        } else {
                            // Handle the error without network response, e.g., network is down or server didn't respond
                            if (isConnected(activity)) callback.onSuccess(false, "")
                            val message = VolleyErrorMessage(volleyError)
                            if (message != "") Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                        if (isProgress) progressDisplay.hideProgress()
                    } catch (e: JSONException) {
                        // Handle a malformed JSON response
                        e.printStackTrace()
                    } catch (error: UnsupportedEncodingException) {
                        error.printStackTrace()
                    }
                }) {
                    override fun getHeaders(): Map<String, String> {
                        val params1: MutableMap<String, String> = HashMap()
                        params1[Constant.AUTHORIZATION] =
                            "Bearer " + session!!.getData(Constant.TOKEN)
                        return params1
                    }

                    override fun getParams(): Map<String, String>? {
                        return params
                    }
                }

            stringRequest.setRetryPolicy(DefaultRetryPolicy(0, 0, 0f))
            instance!!.requestQueue.cache.clear()
            instance!!.addToRequestQueue(stringRequest)
        }

        fun RequestToVolley(
            callback: VolleyCallback,
            activity: Activity,
            url: String?,
            params: Map<String, String>?,
            isProgress: Boolean
        ) {
            if (ProgressDisplay.mProgressBar != null) {
                ProgressDisplay.mProgressBar.visibility = View.GONE
            }
            val progressDisplay = ProgressDisplay(activity)
            progressDisplay.hideProgress()

            if (isProgress) progressDisplay.showProgress()
            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response: String? ->
                    if (isConnected(activity)) callback.onSuccess(true, response)
                    if (isProgress) progressDisplay.hideProgress()
                }, Response.ErrorListener { error: VolleyError? ->
                    if (isProgress) progressDisplay.hideProgress()
                    if (isConnected(activity)) callback.onSuccess(false, "")
                    val message = VolleyErrorMessage(error)
                    if (message != "") Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                }) {
                    override fun getParams(): Map<String, String>? {
                        return params
                    }
                }

            stringRequest.setRetryPolicy(DefaultRetryPolicy(0, 0, 0f))
            instance!!.requestQueue.cache.clear()
            instance!!.addToRequestQueue(stringRequest)
        }

        fun RequestToVolleyMulti(
            callback: VolleyCallback,
            activity: Activity,
            url: String?,
            params: Map<String, String>,
            fileParams: Map<String, String>
        ) {
            if (isConnected(activity)) {
                val multipartRequest: VolleyMultiPartRequest = object : VolleyMultiPartRequest(url,
                    Response.Listener { response: String? -> callback.onSuccess(true, response) },
                    Response.ErrorListener { error: VolleyError? ->
                        callback.onSuccess(
                            false,
                            ""
                        )
                    }) {
                    override fun getDefaultParams(): Map<String, String> {
                        return params
                    }

                    override fun getFileParams(): Map<String, String> {
                        return fileParams
                    }
                }

                multipartRequest.setRetryPolicy(
                    DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
                )
                instance!!.requestQueue.cache.clear()
                instance!!.addToRequestQueue(multipartRequest)
            }
        }

        fun GetVolleyRequest(
            callback: VolleyCallback,
            activity: Activity,
            url: String?,
            params: Map<String, String>?,
            isProgress: Boolean
        ) {
            if (ProgressDisplay.mProgressBar != null) {
                ProgressDisplay.mProgressBar.visibility = View.GONE
            }
            val progressDisplay = ProgressDisplay(activity)
            progressDisplay.hideProgress()

            if (isProgress) progressDisplay.showProgress()
            val stringRequest: StringRequest =
                object : StringRequest(Method.GET, url, Response.Listener { response: String? ->
                    if (isConnected(activity)) callback.onSuccess(true, response)
                    if (isProgress) progressDisplay.hideProgress()
                }, Response.ErrorListener { error: VolleyError? ->
                    if (isProgress) progressDisplay.hideProgress()
                    if (isConnected(activity)) callback.onSuccess(false, "")
                    val message = VolleyErrorMessage(error)
                    if (message != "") Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                }) {
                    override fun getParams(): Map<String, String>? {
                        return params
                    }
                }

            stringRequest.setRetryPolicy(DefaultRetryPolicy(0, 0, 0f))
            instance!!.requestQueue.cache.clear()
            instance!!.addToRequestQueue(stringRequest)
        }

        fun isConnected(activity: Activity): Boolean {
            var check = false
            try {
                val ConnectionManager =
                    activity.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = ConnectionManager.activeNetworkInfo
                if (networkInfo != null && networkInfo.isConnected) {
                    check = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return check
        }
    }
}
