package com.example.utinclusiverestrooms.data

import android.content.Context
import java.io.IOException

class Utils {
    companion object Factory {
        fun create(): Utils = Utils()
    }

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
}