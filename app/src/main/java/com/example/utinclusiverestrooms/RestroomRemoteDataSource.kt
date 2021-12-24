package com.example.utinclusiverestrooms

import android.util.Log
import com.google.gson.Gson
import com.google.gson.stream.JsonToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject

class RestroomRemoteDataSource @Inject constructor() {
    private val utils: Utils = Utils.create()

    suspend fun getRestrooms(): MutableList<Restroom> {

        val restrooms = mutableListOf<Restroom>()
        var jsonFileString = ""
        val url: URL? = try {
            URL("https://services9.arcgis.com/w9x0fkENXvuWZY26/arcgis/rest/services/Gender_Inclusive_Restrooms_View/FeatureServer/0/query?where=0%3D0&outFields=%2A&f=json")
        } catch (e: MalformedURLException){
            Log.d("Exception", e.toString())
            null
        }
        if (url != null)
        {
            val jsonFileStringDeferred = GlobalScope.async {
                try {
                    Log.d("RestroomRemoteData", "Trying to retrieve from URL")
                    url.readText()
                } catch (e: FileNotFoundException) {
                    Log.d("RestroomRemoteData", "Retrieved from local JSON because file not found")
                    utils.getJsonDataFromAsset(MainApplication.applicationContext(), "restrooms.json")!!
                }
            }
            jsonFileString = jsonFileStringDeferred.await()
        }
        else {
            jsonFileString = utils.getJsonDataFromAsset(MainApplication.applicationContext(), "restrooms.json")!!
            Log.d("RestroomRemoteData", "Retrieved from local JSON because of malformed url")
        }
        Log.d("RestroomRemoteData", "jsonFileString set")

        if (jsonFileString != "") {
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