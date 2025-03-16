package com.togethersafe.app.components

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.togethersafe.app.utils.isPermissionGranted
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun LocationPermissionHandler() {
    val activity = LocalActivity.current as ComponentActivity
    val appViewModel: AppViewModel = hiltViewModel(activity)
    val mapViewModel: MapViewModel = hiltViewModel(activity)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) appViewModel.completeRequestPermission(true)
        else {
            mapViewModel.stopTracking()
            appViewModel.completeRequestPermission(false)
            showGoToSettingsDialog(activity)
        }
    }

    val isPermissionRequest by appViewModel.isPermissionRequest.collectAsState()

    LaunchedEffect(isPermissionRequest) {
        if (isPermissionRequest) {
            if (isPermissionGranted(activity)) {
                appViewModel.completeRequestPermission(true)
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}

private fun showGoToSettingsDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Izin Lokasi Diperlukan")
        .setMessage(
            "Anda telah menolak izin lokasi atau menolak izin lokasi secara presisi." +
                    " Silakan aktifkan secara manual di Pengaturan."
        )
        .setPositiveButton("Buka Pengaturan") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
        .setNegativeButton("Batal") { _, _ -> }
        .show()
}
