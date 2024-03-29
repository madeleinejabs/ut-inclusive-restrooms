package com.madisonwabs.utinclusiverestrooms.data

import android.location.Geocoder
import com.madisonwabs.utinclusiverestrooms.MainApplication
import com.google.gson.Gson
import com.google.gson.stream.JsonToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class RestroomRemoteDataSource @Inject constructor() {
    private val utils: Utils = Utils.create()

    suspend fun getRestrooms(): MutableList<Restroom> {
        val restrooms = mutableListOf<Restroom>()
        val jsonFileString: String
        val url: URL? = try {
            withContext(Dispatchers.IO) {
                URL("https://services9.arcgis.com/w9x0fkENXvuWZY26/arcgis/rest/services/Gender_Inclusive_Restrooms_View/FeatureServer/0/query?where=0%3D0&outFields=%2A&f=json")
            }
        } catch (e: MalformedURLException) {
            null
        }
        if (url != null) {
            withContext(Dispatchers.IO) {
                val jsonFileStringDeferred = async {
                    try {
                        url.readText()
                    } catch (e: FileNotFoundException) {
                        utils.getJsonDataFromAsset(
                            MainApplication.applicationContext(),
                            "restrooms.json"
                        )!!
                    }
                }
                jsonFileString = jsonFileStringDeferred.await()
            }
        } else {
            jsonFileString =
                utils.getJsonDataFromAsset(MainApplication.applicationContext(), "restrooms.json")!!
        }

        withContext(Dispatchers.IO) {
            if (jsonFileString != "") {
                val gson = Gson()
                val reader = gson.newJsonReader(jsonFileString.reader())
                reader.beginObject()
                while (reader.hasNext()) {
                    val firstName = reader.nextName()
                    if (!firstName.equals("features")) {
                        reader.skipValue()
                    } else {
                        reader.beginArray()
                        while (reader.hasNext()) {
                            reader.beginObject()
                            while (reader.hasNext()) {
                                val secondName = reader.nextName()
                                if (secondName.equals("attributes")) {
                                    reader.beginObject()
                                    val restroom = Restroom()
                                    while (reader.hasNext()) {
                                        val fieldName = reader.nextName()
                                        if (reader.peek() == JsonToken.NULL) {
                                            reader.skipValue()
                                        } else {
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
                                                "Photo_URL" -> restroom.Photo_URL =
                                                    reader.nextString()
                                                else -> reader.skipValue()
                                            }
                                        }
                                    }
                                    if (restroom.Status == "Active") {
                                        restrooms.add(restroom)
                                    }
                                    reader.endObject()
                                } else {
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
        }

        val gc = Geocoder(MainApplication.applicationContext())
        if (Geocoder.isPresent()) {
            restrooms.forEach {
                // Second condition handles incorrect main tower address
                if (it.Address_Full != "" && it.Address_Full != "W 24th St, Austin, TX 78705") {
                    val restroomLocation = gc.getFromLocationName(it.Address_Full, 1)[0]
                    it.latitude = restroomLocation.latitude
                    it.longitude = restroomLocation.longitude
                } else {
                    val restroomLocation = gc.getFromLocationName(it.Name + " Austin, TX", 1)[0]
                    it.latitude = restroomLocation.latitude
                    it.longitude = restroomLocation.longitude
                }
            }
        }

        return restrooms
    }
}