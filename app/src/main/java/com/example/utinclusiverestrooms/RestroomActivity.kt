package com.example.utinclusiverestrooms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.gson.Gson

class RestroomActivity : AppCompatActivity() {
    private val viewModel: RestroomViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val utils: Utils = Utils.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restroom)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        when (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> {
                Log.d("PermissionCheck", "Permission already granted")
            }
            else -> {
                Log.d("PermissionCheck", "Need to request permission")
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token).addOnSuccessListener {
                location : Location? ->
            if (location != null) {
                Log.d("ChangeActivity",
                    "Latitude: " + location.latitude.toString())
                Log.d("ChangeActivity",
                    "Longitude: " + location.longitude.toString())
                val textView = findViewById<TextView>(R.id.textView).apply {
                    text = location.latitude.toString() + ", "  + location.longitude.toString()
                }
            }
        }

        val jsonFileString = utils.getJsonDataFromAsset(this, "restrooms.json")
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
                                while (reader.hasNext()) {
                                    val name = reader.nextName()
                                    if (name.equals("Name")) {
                                        val building = reader.nextString()
                                        Log.d("JsonReader", building)
                                    }
                                    else {
                                        reader.skipValue()
                                    }
                                }
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
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("PermissionRequest", "Permission granted")
                val intent = Intent(this, RestroomActivity::class.java)
                startActivity(intent)
            }
            else {
                Log.d("PermissionRequest", "Permission denied")
                val textView = findViewById<TextView>(R.id.textView).apply {
                    text = viewModel.testString
                }
            }
        }
}