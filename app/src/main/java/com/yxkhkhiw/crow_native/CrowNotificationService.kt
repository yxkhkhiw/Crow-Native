package com.yxkhkhiw.crow_native

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.yxkhkhiw.crow_native.utils.FetchAverage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrowNotificationService : Service() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var notificationId = 69
    private lateinit var wakeUpAlarm: WakeUpAlarm
    private lateinit var scheduleAlarm: ScheduleAlarm


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = NotificationCompat.Builder(this, "crow_average_channel")
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Crow Native")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
        wakeUpAlarm = WakeUpAlarm(applicationContext)
        scheduleAlarm = ScheduleAlarm(applicationContext)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.START.name -> start()
            Action.UPDATE.name -> update()
            Action.STOP.name -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchAverage = FetchAverage()
                val average = fetchAverage.fetchAverage()
                Log.d("CrowNotificationService", "Start fetched average: $average")

                notificationBuilder.setContentText("%.6f".format(average))

                if ("%.4f".format(average).toFloat() >= 0.7498F) {
                    wakeUpAlarm.setAlarm()
                }
            } catch (e: Exception) {
                Log.e("CrowNotificationService", "Error in coroutine: ${e.message}")

                notificationBuilder.setContentText("%.6f".format(0))
            } finally {
                startForeground(notificationId, notificationBuilder.build())
                scheduleAlarm.setAlarm()
            }
        }
    }

    private fun update() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchAverage = FetchAverage()
                val average = fetchAverage.fetchAverage()
                Log.d("CrowNotificationService", "update Fetched average: $average")

                notificationBuilder.setContentText("%.6f".format(average))

                if ("%.4f".format(average).toFloat() >= 0.7498F) {
                    wakeUpAlarm.setAlarm()
                }
            } catch (e: Exception) {
                Log.e("CrowNotificationService", "Error in coroutine: ${e.message}")

                notificationBuilder.setContentText("%.6f".format(0))
            } finally {
                notificationManager.notify(notificationId, notificationBuilder.build())
                scheduleAlarm.setAlarm()
            }
        }
    }

    private fun stop() {
        Log.d("CrowNotificationService", "Stop")
        wakeUpAlarm.cancelAlarm()
        scheduleAlarm.cancelAlarm()
        stopSelf()
    }

    enum class Action {
        START, STOP, UPDATE
    }
}
