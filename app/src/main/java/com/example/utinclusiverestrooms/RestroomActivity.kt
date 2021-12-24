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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RestroomActivity : AppCompatActivity() {
    private val viewModel: RestroomViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val utils: Utils = Utils.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restroom)
        // try setLifecycleOwner
        // https://stackoverflow.com/questions/59545195/mutablelivedata-not-updating-in-ui

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
                location: Location? ->
            if (location != null) {
                lifecycleScope.launch {
                    viewModel.updateUiState(location)
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        Log.d("RestroomActivity", "In repeatOnLifecycle")
                        viewModel.uiState.collect {
                            Log.d("RestroomActivity", "In collect")
                            val textView = findViewById<TextView>(R.id.textView).apply {
                                Log.d("RestroomActivity", "Refreshing text view")
                                if (viewModel.uiState.value.size > 0) {
                                    text = viewModel.uiState.value[0].Name + " " + viewModel.uiState.value[0].Address_Full + "\n" +
                                            viewModel.uiState.value[1].Name + " " +  viewModel.uiState.value[1].Address_Full + "\n" +
                                            viewModel.uiState.value[2].Name + " " + viewModel.uiState.value[2].Address_Full
                                }
                            }
                        }

                    }
                }

                /*
                 restroomRepository.getRestroom(0)!!.Name + " " +
                                        restroomRepository.getRestroom(0)!!.distanceTo.toString()+ "m\n" +
                                        restroomRepository.getRestroom(1)!!.Name + " " +
                                        restroomRepository.getRestroom(1)!!.distanceTo.toString() + "m\n" +
                                        restroomRepository.getRestroom(2)!!.Name + " " +
                                        restroomRepository.getRestroom(2)!!.distanceTo.toString() + "m\n"
                 */


                /*
                val restrooms = mutableListOf<Restroom>()
                val jsonFileString = utils.getJsonDataFromAsset(this,"restrooms.json")
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

                val gc = Geocoder(this)
                if (Geocoder.isPresent())
                {
                    restrooms.forEach { it ->
                        if (it.Address_Full != "") {
                            val restroomLocation = gc.getFromLocationName(it.Address_Full, 1)[0]
                            var distance = floatArrayOf(-1.0f)
                            distanceBetween(location.latitude, location.longitude,
                                restroomLocation.latitude, restroomLocation.longitude, distance)
                            it.distanceTo = distance[0]
                        }
                    }

                    restrooms.sortBy { it.distanceTo }


                    restrooms.forEach { it ->
                        if (it.Address_Full != "" && it.distanceTo > 0.0f) {
                            Log.d(it.Name, it.distanceTo.toString())
                            Log.d(it.Name, " ")
                        }
                    }
                */
            }
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