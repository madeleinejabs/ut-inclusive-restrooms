package com.example.utinclusiverestrooms

data class Restroom(var OBJECTID: Int = 0, var Name: String = "", var Building_Abbr: String = "",
                    var Address_Full: String = "", var Room_Number: String = "",
                    var Number_Of_Rooms: String = "", var Bathroom_Type: String = "",
                    var Notes: String = "", var Status: String = "", var Photo_URL: String = "",
                    var distanceTo: Float = Float.MAX_VALUE) {

}
