package com.example.utinclusiverestrooms.data

import android.content.Context.MODE_PRIVATE
import android.location.Location
import android.util.Log
import com.example.utinclusiverestrooms.MainApplication
import java.util.*
import javax.inject.Inject

// times between restroom database updates in seconds
const val REFRESH_TIME = 604800  // 1 week

class RestroomRepository @Inject constructor(
    private val restroomRemoteDataSource: RestroomRemoteDataSource,
    private val restroomLocalDataSource: RestroomLocalDataSource
) {
    private var restrooms = mutableListOf<Restroom>()
    private var sorted = false

    suspend fun sortRestrooms(currentLocation: Location) {
        restrooms = restroomLocalDataSource.getRestrooms()

        // timestamp of seconds since epoch (1/1/1970)
        val currentTime = System.currentTimeMillis() / 1000
        Log.d("RestroomRepository", "currentTime is $currentTime")

        val sharedPrefs =MainApplication.applicationContext().getSharedPreferences("prefs", MODE_PRIVATE)
        val lastUpdatedTime = sharedPrefs.getLong("lastUpdatedTime", 0)
        Log.d("RestroomRepository", "lastUpdatedTime is $lastUpdatedTime")

        val timeDiff = currentTime - lastUpdatedTime
        Log.d("RestroomRepository", "difference is $timeDiff")

        if (restrooms.isEmpty() || timeDiff > REFRESH_TIME) {
            with (sharedPrefs.edit()) {
                putLong("lastUpdatedTime", currentTime)
                apply()
            }
                restrooms = restroomRemoteDataSource.getRestrooms()
                restroomLocalDataSource.update(restrooms)
        }
        restrooms.forEach {
            if (it.latitude != Double.MAX_VALUE && it.longitude != Double.MAX_VALUE) {
                val distance = floatArrayOf(-1.0f)
                Location.distanceBetween(
                    currentLocation.latitude, currentLocation.longitude,
                    it.latitude, it.longitude, distance
                )
                it.distanceTo = distance[0]
            }
        }
        restrooms.sortBy { it.distanceTo }

        sorted = true
    }

    fun getRestrooms(): List<Restroom>? {
        if (!sorted) {
            return null
        }
        return Collections.unmodifiableList(restrooms)
    }
}