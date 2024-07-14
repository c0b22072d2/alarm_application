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
    private lateinit var buttonSetAlarm: Button
    private lateinit var buttonStopAlarm: Button
    private lateinit var buttonSnoozeAlarm: Button
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSetAlarm = findViewById(R.id.button_set_alarm)
        buttonStopAlarm = findViewById(R.id.button_stop_alarm)
        buttonSnoozeAlarm = findViewById(R.id.button_snooze_alarm)

        buttonSetAlarm.setOnClickListener {
            showTimePickerDialog()
        }

        buttonStopAlarm.setOnClickListener {
            stopAlarm()
        }

        buttonSnoozeAlarm.setOnClickListener {
            snoozeAlarm()
        }

        updateButtonVisibility(isAlarmRinging = false)
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
        updateButtonVisibility(isAlarmRinging = false)
        Toast.makeText(this, "Alarm stopped", Toast.LENGTH_SHORT).show()
    }

    private fun snoozeAlarm() {
        stopAlarm()

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 10) // 10分後に再設定
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(this, "Alarm snoozed for 10 minutes", Toast.LENGTH_SHORT).show()
    }

    // アラームが鳴ったときに呼ばれるメソッド
    fun onAlarmStart() {
        updateButtonVisibility(isAlarmRinging = true)
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
        mediaPlayer?.apply {
            isLooping = true // ループ再生を設定
            setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
                updateButtonVisibility(isAlarmRinging = false)
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

    private fun updateButtonVisibility(isAlarmRinging: Boolean) {
        if (isAlarmRinging) {
            buttonSetAlarm.visibility = Button.GONE
            buttonStopAlarm.visibility = Button.VISIBLE
            buttonSnoozeAlarm.visibility = Button.VISIBLE
        } else {
            buttonSetAlarm.visibility = Button.VISIBLE
            buttonStopAlarm.visibility = Button.GONE
            buttonSnoozeAlarm.visibility = Button.GONE
        }
    }
}
