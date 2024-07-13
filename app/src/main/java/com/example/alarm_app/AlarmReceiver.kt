package com.example.alarm_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "onReceive called")
        if (context == null) {
            Log.e("AlarmReceiver", "Context is null")
            return
        }

        try {
            val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("start_alarm", true)
            }
            context.startActivity(mainActivityIntent)
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error starting alarm activity", e)
        }
    }
}
