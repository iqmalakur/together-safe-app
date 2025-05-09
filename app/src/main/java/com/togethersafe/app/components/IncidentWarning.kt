package com.togethersafe.app.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mapbox.geojson.Point
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.viewmodels.MapViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun IncidentWarning() {
    val appViewModel: AppViewModel = getViewModel()
    val incidentViewModel: IncidentViewModel = getViewModel()
    val mapViewModel: MapViewModel = getViewModel()

    val userPosition by mapViewModel.userPosition.collectAsState()
    val incidents by incidentViewModel.incidents.collectAsState()
    var isShowToast by remember { mutableStateOf(false) }

    LaunchedEffect(userPosition) {
        userPosition?.let { location ->
            incidents.forEach { incident ->
                val distance = location.distanceTo(incident.latitude, incident.longitude)

                if (distance <= incident.radius) {
                    isShowToast = true
                    return@let
                }
            }
            isShowToast = false
        }
    }

    LaunchedEffect(isShowToast) {
        if (isShowToast) {
            appViewModel.setToastMessage("Anda Memasuki Wilayah Insiden")
        }
    }
}

fun Point.distanceTo(latitude: Double, longitude: Double): Double {
    val R = 6371000.0
    val dLat = Math.toRadians(latitude - this.latitude())
    val dLon = Math.toRadians(longitude - this.longitude())
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(this.latitude())) * cos(Math.toRadians(latitude)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}
