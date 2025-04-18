package com.togethersafe.app.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.togethersafe.app.data.model.DialogState
import com.togethersafe.app.utils.getActivity
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.utils.isPermissionGranted
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun LocationPermissionHandler() {
    val activity = getActivity()
    val appViewModel: AppViewModel = getViewModel()
    val mapViewModel: MapViewModel = getViewModel()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        mapViewModel.setLoadingLocation(false)
        if (isGranted) appViewModel.completeRequestPermission(true)
        else {
            mapViewModel.stopTracking()
            appViewModel.completeRequestPermission(false)
            showGoToSettingsDialog(activity, appViewModel)
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

private fun showGoToSettingsDialog(context: Context, appViewModel: AppViewModel) {
    appViewModel.setDialogState(
        DialogState(
            title = "Izin Lokasi Diperlukan",
            message = "Anda telah menolak izin lokasi atau menolak izin lokasi secara presisi." +
                    " Silakan aktifkan secara manual di Pengaturan.",
            confirmText = "Buka Pengaturan",
            onConfirm = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            },
            onDismiss = {},
        )
    )
}
