package com.gmwapp.dudeways

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle the broadcast message
        Toast.makeText(context, "Broadcast received", Toast.LENGTH_SHORT).show()
    }
}