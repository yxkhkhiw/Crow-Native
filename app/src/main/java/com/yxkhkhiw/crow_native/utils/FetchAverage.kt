package com.yxkhkhiw.crow_native.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class FetchAverage {
    suspend fun fetchAverage(): Float {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://crow-native-server.onrender.com")
                val urlConnection = url.openConnection() as HttpURLConnection

                // Set timeout values
                urlConnection.connectTimeout = 60000 // 60 seconds
                urlConnection.readTimeout = 10000 // 10 seconds

                // Set headers
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.setRequestProperty("Authorization", "CROW=f#wx;IS33UdtV*oXO7L]Qi6eAIT8QW:#<y]@gQ0Ct\$B2faw_H^;gxM{^Czjy+V{N")

                try {
                    val inputStream = urlConnection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    jsonResponse.getDouble("average").toFloat()
                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: Exception) {
                println("Error fetching average: ${e.message}")
                0F
            }
        }
    }
}
