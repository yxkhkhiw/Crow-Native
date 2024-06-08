package com.yxkhkhiw.crow_native.utils

import java.util.Calendar

class TimeUtils {
    fun getTime(): Int {
        val currentDate = Calendar.getInstance()
        val currentMinutes = currentDate.get(Calendar.MINUTE)
        val remainder = currentMinutes % 5
        val delay = (5 - remainder) * 60 * 1000 - currentDate.get(Calendar.SECOND) * 1000 - currentDate.get(
            Calendar.MILLISECOND)

        return delay + 10000
    }
}