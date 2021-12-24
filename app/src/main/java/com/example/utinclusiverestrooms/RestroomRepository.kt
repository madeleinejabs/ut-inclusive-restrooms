package com.example.utinclusiverestrooms

import android.location.Geocoder
import android.location.Location
import android.util.Log
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class RestroomRepository @Inject constructor(
    private val restroomRemoteDataSource: RestroomRemoteDataSource,
    private val restroomLocalDataSource: RestroomLocalDataSource
) {
    private val restroomsMutex = Mutex()
    private var restrooms: MutableList<Restroom> = mutableListOf<Restroom>()
    private var sorted = false

    suspend fun sortRestrooms(currentLocation: Location) {
        Log.d("RestroomRepository", "Fetching local data")
        restrooms = restroomLocalDataSource.getRestrooms()

        if (restrooms.isEmpty()) {
            Log.d("RestroomRepository", "Fetching remote data")
            restrooms = restroomRemoteDataSource.getRestrooms()
            restroomLocalDataSource.update(restrooms)
        }

        val gc = Geocoder(MainApplication.applicationContext())
        if (Geocoder.isPresent()) {
            restrooms.forEach { it ->
                if (it.Address_Full != "") {
                    val restroomLocation = gc.getFromLocationName(it.Address_Full, 1)[0]
                    var distance = floatArrayOf(-1.0f)
                    Location.distanceBetween(
                        currentLocation.latitude, currentLocation.longitude,
                        restroomLocation.latitude, restroomLocation.longitude, distance
                    )
                    it.distanceTo = distance[0]
                }
            }

            restrooms.sortBy { it.distanceTo }

            sorted = true
        }
    }

    suspend fun getRestroom(index: Int): Restroom? {
        if (!sorted || restrooms.size <= index) {
            return null
        }
        return restrooms[index]
    }
}