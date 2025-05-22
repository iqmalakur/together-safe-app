package com.togethersafe.app.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.mapbox.geojson.Point
import com.togethersafe.app.constants.MapConstants.EARTH_RADIUS_METERS
import com.togethersafe.app.utils.IncidentSoundPlayer
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.viewmodels.MapViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun IncidentWarning() {
    val context = LocalContext.current

    val incidentViewModel: IncidentViewModel = getViewModel()
    val mapViewModel: MapViewModel = getViewModel()

    val userPosition by mapViewModel.userPosition.collectAsState()
    val activeIncidentArea by incidentViewModel.activeIncidentArea.collectAsState()
    val incidents by incidentViewModel.incidents.collectAsState()

    val soundPlayer = remember { IncidentSoundPlayer(context) }

    LaunchedEffect(userPosition) {
        userPosition?.let { location ->
            incidents.forEach { incident ->
                val distance = location.distanceTo(incident.latitude, incident.longitude)

                if (distance <= incident.radius && incident.status != "pending") {
                    if (activeIncidentArea != incident) {
                        incidentViewModel.setActiveIncidentArea(incident)
                        soundPlayer.playWarning(incident.riskLevel)
                    }
                    return@let
                }
            }
            if (activeIncidentArea != null) incidentViewModel.setActiveIncidentArea(null)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            soundPlayer.release()
        }
    }
}

fun Point.distanceTo(latitude: Double, longitude: Double): Double {
    val dLat = Math.toRadians(latitude - this.latitude())
    val dLon = Math.toRadians(longitude - this.longitude())
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(this.latitude())) * cos(Math.toRadians(latitude)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return EARTH_RADIUS_METERS * c
}
