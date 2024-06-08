package com.yxkhkhiw.crow_native

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.yxkhkhiw.crow_native.utils.TimeUtils

class ScheduleAlarm(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setAlarm() {
        val intent = Intent(context, CrowNotificationService::class.java).apply { action = CrowNotificationService.Action.UPDATE.name }
        val pendingIntent = PendingIntent.getService(context, 34, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmClockInfo = AlarmClockInfo(
            System.currentTimeMillis() + TimeUtils().getTime(),
//            System.currentTimeMillis() + 10000,

            pendingIntent
        )

        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    fun cancelAlarm() {
        val intent = Intent(context, CrowNotificationService::class.java).apply { action = CrowNotificationService.Action.UPDATE.name }
        val pendingIntent = PendingIntent.getService(context, 34, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
    }
}
