package com.madisonwabs.utinclusiverestrooms.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Restroom::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun restroomDao(): RestroomDao
}