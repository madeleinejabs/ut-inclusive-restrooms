package com.madisonwabs.utinclusiverestrooms.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestroomDao {
    @Query("SELECT * FROM restroom")
    suspend fun getAll(): MutableList<Restroom>

    @Query("DELETE FROM restroom")
    suspend fun clearTable()

    @Insert
    suspend fun insertAll(vararg restrooms: Restroom)
}