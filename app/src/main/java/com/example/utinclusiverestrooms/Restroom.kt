package com.example.utinclusiverestrooms

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Restroom(@PrimaryKey var OBJECTID: Int = 0,
                    @ColumnInfo(name="Name") var Name: String = "",
                    @ColumnInfo(name="Building_Abbr") var Building_Abbr: String = "",
                    @ColumnInfo(name="Address_Full") var Address_Full: String = "",
                    @ColumnInfo(name="Room_Number") var Room_Number: String = "",
                    @ColumnInfo(name="Number_Of_Rooms") var Number_Of_Rooms: String = "",
                    @ColumnInfo(name="Bathroom_Type") var Bathroom_Type: String = "",
                    @ColumnInfo(name="Notes") var Notes: String = "",
                    @ColumnInfo(name="Status") var Status: String = "",
                    @ColumnInfo(name="Photo_URL") var Photo_URL: String = "",
                    @ColumnInfo(name="distanceTo") var distanceTo: Float = Float.MAX_VALUE) {

}
