package com.example.alarm_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "onReceive called")
        if (context == null) {
            Log.e("AlarmReceiver", "Context is null")
            return
        }

        try {
            // MediaPlayerの初期化
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
            mediaPlayer?.apply {
                setOnCompletionListener { mp ->
                    mp.release()
                    Log.d("AlarmReceiver", "MediaPlayer released")
                }
                start()
                Log.d("AlarmReceiver", "MediaPlayer started")
            }
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error playing alarm sound", e)
        }
    }
}
