package com.togethersafe.app.views.map

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.togethersafe.app.R
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun Annotations() {
    val mapViewModel: MapViewModel = getViewModel()

    val userPosition by mapViewModel.userPosition.collectAsState()
    val destination by mapViewModel.destination.collectAsState()

    if (userPosition != null) UserPosition(mapViewModel)
    if (destination != null) Destination(mapViewModel)
    IncidentMarkers()
}

@Composable
private fun UserPosition(mapViewModel: MapViewModel) {
    val userPosition by mapViewModel.userPosition.collectAsState()

    CircleAnnotation(userPosition!!) {
        circleRadius = 16.0
        circleColor = Color(0x553498DB)
        circleStrokeWidth = 0.0
    }

    CircleAnnotation(userPosition!!) {
        circleRadius = 8.0
        circleColor = Color.Cyan
        circleStrokeWidth = 2.0
        circleStrokeColor = Color.Black
    }
}

@Composable
private fun Destination(mapViewModel: MapViewModel) {
    val destination by mapViewModel.destination.collectAsState()
    val markerIconDrawable = R.drawable.ic_marker
    val marker = rememberIconImage(
        key = markerIconDrawable,
        painter = painterResource(markerIconDrawable)
    )

    PointAnnotation(destination!!) {
        iconImage = marker
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IncidentMarkers() {
    val incidentViewModel: IncidentViewModel = getViewModel()
    val incidents by incidentViewModel.incidents.collectAsState()
    val selectedIncident by incidentViewModel.selectedIncident.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    incidents.forEach { incident ->
        CircleAnnotation(Point.fromLngLat(incident.longitude, incident.latitude)) {
            circleRadius = 8.0
            circleColor = getRiskLevelColor(incident.riskLevel)
            circleStrokeWidth = 2.0
            circleStrokeColor = Color.Black

            interactionsState.onClicked {
                incidentViewModel.setSelectedIncident(incident)
                true
            }
        }
    }

    if (selectedIncident != null) {
        BottomSheet(sheetState)
    }
}

private fun getRiskLevelColor(riskLevel: String): Color =
    when (riskLevel) {
        "high" -> Color.Red
        "medium" -> Color.Yellow
        else -> Color.Blue
    }
