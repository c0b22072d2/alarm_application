package com.example.alarm_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class MainActivity : AppCompatActivity(), TimePickerFragment.TimePickerDialogListener {
    private lateinit var buttonStopAlarm: Button
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSetAlarm: Button = findViewById(R.id.button_set_alarm)
        buttonStopAlarm = findViewById(R.id.button_stop_alarm)

        buttonSetAlarm.setOnClickListener {
            showTimePickerDialog()
        }

        buttonStopAlarm.setOnClickListener {
            stopAlarm()
        }
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerFragment()
        timePicker.show(supportFragmentManager, "timePicker")
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        setAlarm(hourOfDay, minute)
    }

    private fun setAlarm(hourOfDay: Int, minute: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(this, "Alarm set for ${hourOfDay}:${minute}", Toast.LENGTH_SHORT).show()
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        buttonStopAlarm.visibility = Button.GONE
        Toast.makeText(this, "Alarm stopped", Toast.LENGTH_SHORT).show()
    }

    // アラームが鳴ったときに呼ばれるメソッド
    fun onAlarmStart() {
        buttonStopAlarm.visibility = Button.VISIBLE
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
        mediaPlayer?.apply {
            isLooping = true // ループ再生を設定
            setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
                buttonStopAlarm.visibility = Button.GONE
            }
            start()
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent?.getBooleanExtra("start_alarm", false) == true) {
            onAlarmStart()
        }
    }
}
