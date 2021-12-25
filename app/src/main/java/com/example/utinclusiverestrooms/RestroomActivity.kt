package com.example.utinclusiverestrooms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

@AndroidEntryPoint
class RestroomActivity : AppCompatActivity() {
    private val viewModel: RestroomViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restroom)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        when (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_DENIED -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token).addOnSuccessListener {
                location: Location? ->
            if (location != null) {
                lifecycleScope.launch {
                    viewModel.updateUiState(location, 1)
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.uiState.collect {
                            findViewById<TextView>(R.id.textView).apply {
                                if (viewModel.uiState.value.isNotEmpty()) {
                                    text = getString(R.string.restroom_info, viewModel.uiState.value[0].Name, viewModel.uiState.value[0].distanceTo)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                val intent = Intent(this, RestroomActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                findViewById<TextView>(R.id.textView).apply {
                    text = getString(R.string.location_denied)
                }
            }
        }
}