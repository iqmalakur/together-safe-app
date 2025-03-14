package com.togethersafe.app

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.togethersafe.app.components.DoubleBackHandler
import com.togethersafe.app.utils.getCurrentLocation
import com.togethersafe.app.utils.isPermissionGranted
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.views.map.MapScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()
    private val incidentViewModel: IncidentViewModel by viewModels()

    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) appViewModel.completeRequestPermission(true)
            else {
                appViewModel.completeRequestPermission(false)
                showGoToSettingsDialog()
            }
        }

        if (isPermissionGranted(this)) getCurrentLocation(this) {}

        incidentViewModel.loadIncidents()

        setContent {
            val isPermissionRequest by appViewModel.isPermissionRequest.collectAsState()
            val toastMessage by appViewModel.toastMessage.collectAsState()
            val errorIncident by incidentViewModel.error.collectAsState()

            LaunchedEffect(toastMessage) {
                if (toastMessage.isNotBlank()) {
                    showToast(toastMessage)
                    appViewModel.setToastMessage("")
                }
            }

            LaunchedEffect(isPermissionRequest) {
                if (isPermissionRequest) {
                    requestLocationPermission()
                }
            }

            LaunchedEffect(errorIncident) {
                if (errorIncident != null) appViewModel.setToastMessage(errorIncident!!)
            }

            MapScreen()

            DoubleBackHandler()
        }
    }

    private fun requestLocationPermission() {
        if (isPermissionGranted(this)) {
            appViewModel.completeRequestPermission(true)
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showGoToSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Izin Lokasi Diperlukan")
            .setMessage(
                "Anda telah menolak izin lokasi atau menolak izin lokasi secara presisi." +
                        " Silakan aktifkan secara manual di Pengaturan."
            )
            .setPositiveButton("Buka Pengaturan") { _, _ -> openAppSettings() }
            .setNegativeButton("Batal") { _, _ -> }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
