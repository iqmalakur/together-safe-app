package com.togethersafe.app

import android.content.Context
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.togethersafe.app.ui.components.MapButtons
import com.togethersafe.app.ui.components.MapHeader
import com.togethersafe.app.ui.view.MapScreen
import com.togethersafe.app.ui.viewmodel.LocationViewModel
import com.togethersafe.app.utils.checkLocationPermission
import com.togethersafe.app.utils.getCurrentLocation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val locationViewModel: LocationViewModel by viewModels()

    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) locationViewModel.completeRequestPermission(true)
            else {
                locationViewModel.completeRequestPermission(false)
                showGoToSettingsDialog()
            }
        }

        if (checkLocationPermission(context)) getCurrentLocation(context) {}

        setContent {
            val isPermissionRequest by locationViewModel.isPermissionRequest.collectAsState()
            var showLocationState by remember { mutableStateOf(false) }

            LaunchedEffect(isPermissionRequest) {
                if (isPermissionRequest) { requestLocationPermission() }
            }

            Box {
                MapScreen(
                    context = context,
                    showLocation = showLocationState,
                    resetShowLocation = { showLocationState = false },
                )
                MapHeader()
                MapButtons(
                    showLocationClick = { showLocationState = true },
                )
            }
        }
    }

    private fun requestLocationPermission() {
        if (checkLocationPermission(this)) {
            locationViewModel.completeRequestPermission(true)
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showGoToSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Izin Lokasi Diperlukan")
            .setMessage("Anda telah menolak izin lokasi atau menolak izin lokasi secara presisi." +
                    " Silakan aktifkan secara manual di Pengaturan.")
            .setPositiveButton("Buka Pengaturan") { _, _ -> openAppSettings() }
            .setNegativeButton("Batal") { _, _ ->  }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}
