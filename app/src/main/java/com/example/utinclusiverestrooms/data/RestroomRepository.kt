package com.example.utinclusiverestrooms.data

import android.location.Location
import java.util.*
import javax.inject.Inject

class RestroomRepository @Inject constructor(
    private val restroomRemoteDataSource: RestroomRemoteDataSource,
    private val restroomLocalDataSource: RestroomLocalDataSource
) {
    private var restrooms = mutableListOf<Restroom>()
    private var sorted = false

    suspend fun sortRestrooms(currentLocation: Location) {
        restrooms = restroomLocalDataSource.getRestrooms()

        if (restrooms.isEmpty()) { // todo: implement periodic refresh
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