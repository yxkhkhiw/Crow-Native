package com.yxkhkhiw.crow_native

import android.content.Context
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log

class WakeUpAlarm (private val context: Context) {
    private  val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setAlarm() {
        val intent = Intent(context, WakeUpAlarmService::class.java).apply { action = WakeUpAlarmService.Action.START.name }
        val pendingIntent = PendingIntent.getService(context, 33, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            System.currentTimeMillis() + 500,
            pendingIntent
        )

        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        Log.d("WakeUpAlarm", "Start alarm")
    }

    fun cancelAlarm() {
        Intent(context, WakeUpAlarmService::class.java).apply {
            action = WakeUpAlarmService.Action.STOP.name
            context.startService(this)
        }

        Log.d("WakeUpAlarm", "Stop alarm")
    }
}