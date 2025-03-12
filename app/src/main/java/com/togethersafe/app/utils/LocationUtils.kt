package com.togethersafe.app.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.togethersafe.app.ui.viewmodel.MapViewModel
import com.togethersafe.app.utils.MapConfig.ZOOM_DEFAULT

fun checkLocationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun getCurrentLocation(context: Context, onLocationReceived: (Location) -> Unit) {
    if (checkLocationPermission(context)) {
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

private fun isLocationSame(first: Location, second: Location): Boolean {
    val isLatitudeSame = first.latitude == second.latitude
    val isLongitudeSame = first.longitude == second.longitude
    return isLatitudeSame && isLongitudeSame
}

@Composable
fun GetUserLocation(
    context: Context,
    mapViewModel: MapViewModel = hiltViewModel(),
) {
    val isTracking by mapViewModel.isTracking.collectAsState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
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
        if (checkLocationPermission(context)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    lastKnownLocation = lastLocation
                    updateLocation(lastLocation)
                }
            }

            getCurrentLocation(context) { newLocation ->
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


// TODO: fix for location tracking
@Composable
fun StartRealTimeLocationUpdates(
    context: Context,
    onLocationUpdate: (Location) -> Unit
): LocationCallback {
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 2000)
        .setMinUpdateIntervalMillis(1000)
        .build()

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onLocationUpdate(location)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (checkLocationPermission(context)) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    return locationCallback
}

// Fungsi untuk menghentikan update lokasi real-time
fun StopRealTimeLocationUpdates(context: Context, locationCallback: LocationCallback) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.removeLocationUpdates(locationCallback)
}
