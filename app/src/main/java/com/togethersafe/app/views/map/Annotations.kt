package com.togethersafe.app.views.map

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.togethersafe.app.utils.DestinationAnnotation
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun Annotations() {
    val mapViewModel: MapViewModel = getViewModel()

    val userPosition by mapViewModel.userPosition.collectAsState()
    val destination by mapViewModel.destination.collectAsState()

    if (userPosition != null) UserPosition(mapViewModel)
    if (destination != null) DestinationAnnotation(destination!!)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IncidentMarkers() {
    val appViewModel: AppViewModel = getViewModel()
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
                appViewModel.setLoading(true)

                incidentViewModel.fetchIncidentById(
                    id = incident.id,
                    onError = { _, errors -> appViewModel.setToastMessage(errors[0]) })
                { appViewModel.setLoading(false) }

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
