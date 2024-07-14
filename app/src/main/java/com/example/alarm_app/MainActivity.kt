package com.example.alarm_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class MainActivity : AppCompatActivity(), TimePickerFragment.TimePickerDialogListener {
    private lateinit var buttonStopAlarm: Button
    private lateinit var alarmContainer: LinearLayout
    private var mediaPlayer: MediaPlayer? = null
    private var alarmCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSetAlarm: Button = findViewById(R.id.button_set_alarm)
        buttonStopAlarm = findViewById(R.id.button_stop_alarm)
        alarmContainer = findViewById(R.id.alarm_container)

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
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // リクエストコードを動的に生成
        val requestCode = alarmCounter++
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("requestCode", requestCode) // リクエストコードをIntentに追加

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val alarmTextView = TextView(this).apply {
            text = "Alarm set for ${String.format("%02d", hourOfDay)}:${String.format("%02d", minute)}"
            textSize = 24f
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
            setPadding(0, 8, 0, 8)
        }
        alarmContainer.addView(alarmTextView)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(this, "Alarm set for ${String.format("%02d", hourOfDay)}:${String.format("%02d", minute)}", Toast.LENGTH_SHORT).show()
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        buttonStopAlarm.visibility = Button.GONE

        alarmContainer.removeAllViews() // アラーム表示をクリア

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
