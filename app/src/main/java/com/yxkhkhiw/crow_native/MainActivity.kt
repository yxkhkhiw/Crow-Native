package com.yxkhkhiw.crow_native

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import com.yxkhkhiw.crow_native.utils.ServiceUtils

class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onPause() {
        super.onPause()
        mainActivityViewModel.stop()

        Log.d("MainActivity", "onPause")
    }

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.start()
        updateOnOffButtonText()

        Log.d("MainActivity", "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivityViewModel.stop()

        Log.d("MainActivity", "onDestroy")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textAverage: TextView = findViewById(R.id.textAverage)
        mainActivityViewModel.average.observe(this) { average ->
            "%.6f".format(average).also { textAverage.text = it }
        }

        val onButton: Button = findViewById(R.id.onOffBtn)
        onButton.setOnClickListener {
            val newText = if (onButton.text.toString() == TEXT_ON) TEXT_OFF else TEXT_ON
            updateOnOffButtonText(newText)
            val action = if (newText == TEXT_ON) CrowNotificationService.Action.STOP else CrowNotificationService.Action.START
            startCrowNotificationService(action)
        }

        val testButton: Button = findViewById(R.id.testBtn)
        testButton.setOnClickListener {
            val newText = if (testButton.text.toString() == TEST_TEXT_ON) TEST_TEXT_OFF else TEST_TEXT_ON
            testAlarm(newText)
        }

        Log.d("MainActivity", "onCreate")
    }

    private fun updateOnOffButtonText(newText: String? = null) {
        val onButton: Button = findViewById(R.id.onOffBtn)
        onButton.text = newText ?: if (ServiceUtils.isServiceRunning(this, CrowNotificationService::class.java)) TEXT_OFF else TEXT_ON
    }

    @SuppressLint("BatteryLife")
    private fun startCrowNotificationService(action: CrowNotificationService.Action) {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).also {
                it.data = Uri.parse("package:$packageName")
                startActivity(it)
            }
            return
        }

        Intent(this@MainActivity, CrowNotificationService::class.java).also {
            it.action = action.toString()
            startService(it)
        }
    }

    private fun testAlarm(newText: String) {
        val testButton: Button = findViewById(R.id.testBtn)
        val wakeUpAlarm = WakeUpAlarm(this@MainActivity)
        if (newText == TEST_TEXT_ON) {
            wakeUpAlarm.cancelAlarm()
        } else {
            wakeUpAlarm.setAlarm()
        }
        testButton.text = newText
    }

    companion object {
        private const val TEXT_ON = "Set Alarm"
        private const val TEXT_OFF = "Cancel Alarm"
        private const val TEST_TEXT_ON = "Test Alarm"
        private const val TEST_TEXT_OFF = "Stop Alarm"
    }
}