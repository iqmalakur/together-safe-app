package com.togethersafe.app.utils

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.togethersafe.app.data.model.DialogState
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@SuppressLint("MissingPermission")
@Composable
fun GetUserLocation() {
    val activity = LocalActivity.current as ComponentActivity

    val appViewModel: AppViewModel = getViewModel()
    val mapViewModel: MapViewModel = getViewModel()

    val isLoadingLocation by mapViewModel.isLoadingLocation.collectAsState()
    val isTracking by mapViewModel.isTracking.collectAsState()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(activity) }
    var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let { location ->
                    lastKnownLocation = location
                    val latitude = location.latitude
                    val longitude = location.longitude
                    mapViewModel.setUserPosition(latitude, longitude)
                    if (isTracking) mapViewModel.setCameraPosition(latitude, longitude)
                    if (isLoadingLocation) mapViewModel.setLoadingLocation(false)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (isPermissionGranted(activity)) {
            if (!isLocationEnabled(activity)) {
                mapViewModel.setLoadingLocation(false)
                promptEnableGPS(activity, appViewModel)
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    lastKnownLocation = lastLocation
                    val lat = lastLocation.latitude
                    val lon = lastLocation.longitude
                    mapViewModel.setUserPosition(lat, lon)
                    if (isTracking) mapViewModel.setCameraPosition(lat, lon)
                    if (isLoadingLocation) mapViewModel.setLoadingLocation(false)
                }
            }

            val locationRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 3000)
                .setMinUpdateIntervalMillis(1000)
                .setWaitForAccurateLocation(false)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }
}

fun isPermissionGranted(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

fun promptEnableGPS(context: Context, appViewModel: AppViewModel) {
    appViewModel.setDialogState(
        DialogState(
            title = "Aktifkan Lokasi",
            message = "Fitur ini memerlukan GPS. Silakan aktifkan GPS untuk melanjutkan.",
            confirmText = "Pengaturan",
            onConfirm = {
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            },
            onDismiss = {}
        )
    )
}

private fun isLocationSame(first: Location, second: Location): Boolean {
    val isLatitudeSame = first.latitude == second.latitude
    val isLongitudeSame = first.longitude == second.longitude
    return isLatitudeSame && isLongitudeSame
}
