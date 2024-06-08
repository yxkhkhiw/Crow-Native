package com.yxkhkhiw.crow_native

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yxkhkhiw.crow_native.utils.FetchAverage
import com.yxkhkhiw.crow_native.utils.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    private val _average = MutableLiveData<Float>()
    val average: LiveData<Float>
        get() = _average

    private var fetchJob: Job? = null

    fun start() {
        stop()  // Ensure any previous job is cancelled before starting a new one
        fetchJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    val fetchAverage = FetchAverage()
                    val average = fetchAverage.fetchAverage()
                    _average.postValue(average)

                    Log.d("MainActivityViewModel", average.toString())

                } catch (e: Exception) {
                    Log.e("MainActivityViewModel", "Error in coroutine: ${e.message}")
                }

                delay(TimeUtils().getTime().toLong())
            }
        }

        Log.d("MainActivityViewModel", "fun start()")
    }

    fun stop() {
        fetchJob?.cancel() // Cancel the existing job if it is running
        fetchJob = null

        Log.d("MainActivityViewModel", "fun stop()")
    }

    override fun onCleared() {
        super.onCleared()

        stop()  // Ensure the job is cancelled when the ViewModel is cleared
    }
}
