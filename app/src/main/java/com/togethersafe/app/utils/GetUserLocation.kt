package com.togethersafe.app.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.togethersafe.app.constants.MapConstants.ZOOM_DEFAULT
import com.togethersafe.app.viewmodels.MapViewModel

@SuppressLint("MissingPermission")
@Composable
fun GetUserLocation() {
    val activity = LocalActivity.current as ComponentActivity
    val mapViewModel: MapViewModel = hiltViewModel(activity)
    val isTracking by mapViewModel.isTracking.collectAsState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(activity) }
    var lastKnownLocation by remember { mutableStateOf<Location?>(null) }

    val updateLocation: (Location) -> Unit = { location ->
        val latitude = location.latitude
        val longitude = location.longitude
        mapViewModel.setUserPosition(latitude, longitude)
        if (isTracking) {
            mapViewModel.setZoomLevel(ZOOM_DEFAULT)
            mapViewModel.setCameraPosition(latitude, longitude)
        }
    }

    LaunchedEffect(Unit) {
        if (isPermissionGranted(activity)) {
            if (!isLocationEnabled(activity)) {
                promptEnableGPS(activity)
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    lastKnownLocation = lastLocation
                    updateLocation(lastLocation)
                }
            }

            getCurrentLocation(activity) { newLocation ->
                var update = false

                if (lastKnownLocation == null) update = true
                lastKnownLocation?.let {
                    if (!isLocationSame(newLocation, it)) update = true
                }

                if (update) {
                    lastKnownLocation = newLocation
                    updateLocation(newLocation)
                }
            }
        }
    }
}

fun isPermissionGranted(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onLocationReceived: (Location) -> Unit) {
    if (isPermissionGranted(context)) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let { newLocation ->
                    onLocationReceived(newLocation)
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

fun promptEnableGPS(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Aktifkan Lokasi")
        .setMessage("Fitur ini memerlukan GPS. Silakan aktifkan GPS untuk melanjutkan.")
        .setPositiveButton("Pengaturan") { _, _ ->
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        .setNegativeButton("Batal", null)
        .show()
}

private fun isLocationSame(first: Location, second: Location): Boolean {
    val isLatitudeSame = first.latitude == second.latitude
    val isLongitudeSame = first.longitude == second.longitude
    return isLatitudeSame && isLongitudeSame
}
