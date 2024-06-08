package com.yxkhkhiw.crow_native

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat

class WakeUpAlarmService : Service() {
    private val mediaPlayers: MutableList<MediaPlayer> = mutableListOf()
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.START.name -> startAlarm()
            Action.STOP.name -> stopAlarm()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        stopAlarm()
    }

    private fun startAlarm() {
        val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val mediaPlayer = MediaPlayer().apply {
            setDataSource(applicationContext, ringtoneUri)
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
            isLooping = true
            prepare()
            start()

            // Set the completion listener to stop the service when the sound finishes
            setOnCompletionListener {
                stopSelf()
            }
        }

        mediaPlayers.add(mediaPlayer)

        // Start vibration
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = ContextCompat.getSystemService(this, VibratorManager::class.java)
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        val vibrationPattern = longArrayOf(0, 1000, 1000) // delay, vibrate, sleep
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(vibrationPattern, 0)
            vibrator?.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(vibrationPattern, 0)
        }
    }

    private fun stopAlarm() {
        mediaPlayers.forEach { mediaPlayer ->
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        mediaPlayers.clear()
        vibrator?.cancel()
    }

    enum class Action {
        START, STOP
    }
}