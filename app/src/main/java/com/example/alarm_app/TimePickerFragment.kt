package com.example.alarm_app

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    interface TimePickerDialogListener {
        fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 現在の時刻をデフォルトの時刻として設定
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // 新しいTimePickerDialogインスタンスを作成して返す
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // 呼び出し元のアクティビティに時間を設定する
        val activity = activity as TimePickerDialogListener
        activity.onTimeSet(view, hourOfDay, minute)
    }
}
