package com.example.utinclusiverestrooms

import com.google.gson.Gson
import com.google.gson.stream.JsonToken
import javax.inject.Inject

class RestroomRemoteDataSource @Inject constructor() {
    private val utils: Utils = Utils.create()

    suspend fun getRestrooms(): MutableList<Restroom> {
        val restrooms = mutableListOf<Restroom>()
        val jsonFileString = utils.getJsonDataFromAsset(MainApplication.applicationContext(),"restrooms.json")
        if (jsonFileString != null) {
            val gson = Gson()
            val reader = gson.newJsonReader(jsonFileString.reader())
            reader.beginObject()
            while (reader.hasNext()) {
                val name = reader.nextName()
                if (!name.equals("features")) {
                    reader.skipValue()
                }
                else {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        reader.beginObject()
                        while (reader.hasNext()) {
                            val name = reader.nextName()
                            if (name.equals("attributes")) {
                                reader.beginObject()
                                val restroom = Restroom()
                                while (reader.hasNext()) {
                                    val fieldName = reader.nextName()
                                    if (reader.peek() == JsonToken.NULL) {
                                        reader.skipValue()
                                    }
                                    else {
                                        when (fieldName) {
                                            "OBJECTID" -> restroom.OBJECTID = reader.nextInt()
                                            "Name" -> restroom.Name = reader.nextString()
                                            "Building_Abbr" -> restroom.Building_Abbr =
                                                reader.nextString()
                                            "Address_Full" -> restroom.Address_Full =
                                                reader.nextString()
                                            "Room_Number" -> restroom.Room_Number =
                                                reader.nextString()
                                            "Number_Of_Rooms" -> restroom.Number_Of_Rooms =
                                                reader.nextString()
                                            "Bathroom_Type" -> restroom.Bathroom_Type =
                                                reader.nextString()
                                            "Notes" -> restroom.Notes = reader.nextString()
                                            "Status" -> restroom.Status = reader.nextString()
                                            "Photo_URL" -> restroom.Photo_URL = reader.nextString()
                                            else -> reader.skipValue()
                                        }
                                    }
                                }
                                restrooms.add(restroom)
                                reader.endObject()
                            }
                            else {
                                reader.skipValue()
                            }
                        }
                        reader.endObject()
                    }
                    reader.endArray()
                }
            }
            reader.endObject()
        }
        return restrooms
    }
}