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
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.togethersafe.app.data.network.ApiService
import com.togethersafe.app.repository.IncidentRepository
import com.togethersafe.app.ui.components.MapButtons
import com.togethersafe.app.ui.components.MapHeader
import com.togethersafe.app.ui.view.MapScreen
import com.togethersafe.app.ui.viewmodel.IncidentViewModel
import com.togethersafe.app.utils.MapConfig.ZOOM_MAX
import com.togethersafe.app.utils.MapConfig.ZOOM_MIN
import com.togethersafe.app.utils.MapConfig.ZOOM_DEFAULT
import com.togethersafe.app.utils.MapConfig.ZOOM_STEP
import com.togethersafe.app.utils.checkLocationPermission
import com.togethersafe.app.utils.getCurrentLocation
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var context: Context

    private lateinit var onRequestPermissionSuccess: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) onRequestPermissionSuccess()
            else showGoToSettingsDialog()
        }

        if (checkLocationPermission(context)) getCurrentLocation(context) {}

        setContent {
            var zoomState by remember { mutableDoubleStateOf(ZOOM_DEFAULT) }
            var showLocationState by remember { mutableStateOf(false) }

            Box {
                MapScreen(
                    context = context,
                    zoom = zoomState,
                    showLocation = showLocationState,
                    resetShowLocation = { showLocationState = false },
                    requestLocationPermission = ::requestLocationPermission,
                )
                MapHeader()
                MapButtons(
                    showLocationClick = { showLocationState = true },
                    zoomInClick = { if (zoomState < ZOOM_MAX) zoomState += ZOOM_STEP },
                    zoomOutClick = { if (zoomState > ZOOM_MIN) zoomState -= ZOOM_STEP },
                )
            }
        }
    }

    private fun requestLocationPermission(onSuccess: () -> Unit) {
        onRequestPermissionSuccess = onSuccess

        if (checkLocationPermission(this)) {
            onRequestPermissionSuccess()
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
