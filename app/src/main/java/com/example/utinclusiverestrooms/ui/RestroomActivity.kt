package com.example.utinclusiverestrooms.ui

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.utinclusiverestrooms.R
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


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restroom)

        val restroomAdapter = RestroomAdapter(viewModel.uiState.value)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = restroomAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

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
                    viewModel.updateUiState(location)
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.uiState.collect {
                            Log.d("RestroomActivity", "Setting data of restroomAdapter")
                            restroomAdapter.setData(viewModel.uiState.value)
                            restroomAdapter.notifyDataSetChanged()
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