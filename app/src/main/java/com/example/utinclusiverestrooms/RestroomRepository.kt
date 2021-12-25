package com.example.utinclusiverestrooms

import android.location.Location
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

    fun getRestroom(index: Int): Restroom? {
        if (!sorted || restrooms.size <= index) {
            return null
        }
        return restrooms[index]
    }
}