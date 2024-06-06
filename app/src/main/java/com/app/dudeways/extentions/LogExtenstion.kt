package com.app.dudeways.extentions

import android.app.Activity
import android.util.Log

fun Activity.logError(
    tag: String,
    error: String
) = Log.e(tag, error)

fun Activity.logInfo(
    tag: String,
    message: String
) = Log.i(tag, message)
