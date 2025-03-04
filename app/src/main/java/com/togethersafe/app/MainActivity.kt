package com.togethersafe.app

import android.content.Context
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
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.togethersafe.app.ui.components.MapHeader
import com.togethersafe.app.ui.view.MapScreen
import com.togethersafe.app.ui.viewmodel.AppViewModel
import com.togethersafe.app.utils.checkLocationPermission
import com.togethersafe.app.utils.getCurrentLocation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) appViewModel.completeRequestPermission(true)
            else {
                appViewModel.completeRequestPermission(false)
                showGoToSettingsDialog()
            }
        }

        if (checkLocationPermission(context)) getCurrentLocation(context) {}

        setContent {
            val isPermissionRequest by appViewModel.isPermissionRequest.collectAsState()
            val toastMessage by appViewModel.toastMessage.collectAsState()

            if (toastMessage.isNotBlank()) {
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
                appViewModel.setToastMessage("")
            }

            LaunchedEffect(isPermissionRequest) {
                if (isPermissionRequest) { requestLocationPermission() }
            }

            Box {
                MapScreen(context = context)
                MapHeader()
            }
        }
    }

    private fun requestLocationPermission() {
        if (checkLocationPermission(this)) {
            appViewModel.completeRequestPermission(true)
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
