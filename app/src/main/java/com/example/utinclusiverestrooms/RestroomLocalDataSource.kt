package com.example.utinclusiverestrooms

import androidx.room.Room
import javax.inject.Inject

class RestroomLocalDataSource @Inject constructor() {
        private val db = Room.databaseBuilder(
            MainApplication.applicationContext(),
            AppDatabase::class.java, "restrooms"
        ).build()
        private val restroomDao = db.restroomDao()

        suspend fun getRestrooms(): MutableList<Restroom> {
            return restroomDao.getAll()
        }

        suspend fun update(restrooms : MutableList<Restroom>) {
            restroomDao.clearTable()
            restroomDao.insertAll(*restrooms.toTypedArray())
        }
}