package com.togethersafe.app.ui.view

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.togethersafe.app.data.model.Incident
import com.togethersafe.app.ui.components.BottomSheet
import com.togethersafe.app.ui.viewmodel.IncidentViewModel
import com.togethersafe.app.utils.GetUserLocation
import com.togethersafe.app.utils.MapConfig.BEARING
import com.togethersafe.app.utils.MapConfig.LATITUDE_DEFAULT
import com.togethersafe.app.utils.MapConfig.LONGITUDE_DEFAULT
import com.togethersafe.app.utils.MapConfig.PITCH
import com.togethersafe.app.utils.MapConfig.ZOOM_DEFAULT
import com.togethersafe.app.utils.MapConfig.ZOOM_MAX
import com.togethersafe.app.utils.MapConfig.ZOOM_MIN

@Composable
fun MapScreen(
    context: Context,
    viewModel: IncidentViewModel,
    zoom: Double,
    showLocation: Boolean,
    resetShowLocation: () -> Unit,
    requestLocationPermission: (onSuccess: () -> Unit) -> Unit
) {
    var location by remember { mutableStateOf(Point.fromLngLat(LONGITUDE_DEFAULT, LATITUDE_DEFAULT)) }
    var isTrackUser by remember { mutableStateOf(false) }
    var isLocationPermissionGranted by remember { mutableStateOf(false) }

    val incidents by viewModel.incidents.collectAsState()
    val mapViewportState = createMapViewportState(location)
    val setCameraOptions: (CameraOptions.Builder) -> Unit = { cameraOptions ->
        mapViewportState.flyTo(cameraOptions.build())
    }

    if (isLocationPermissionGranted) {
        GetUserLocation(context) { currentLocation ->
            location = Point.fromLngLat(currentLocation.longitude, currentLocation.latitude)
            setCameraOptions(CameraOptions.Builder().center(location))
        }
        isLocationPermissionGranted = false
    }

    LaunchedEffect(Unit) {
        viewModel.loadIncidents()
    }

    LaunchedEffect(zoom) { setCameraOptions(CameraOptions.Builder().zoom(zoom)) }
    LaunchedEffect(showLocation) {
        if (showLocation) {
            isTrackUser = true
            requestLocationPermission {
                isLocationPermissionGranted = true
            }
            resetShowLocation()
        }
    }

    Map(
        mapViewportState = mapViewportState,
        location = location,
        isTrackUser = isTrackUser,
        incidents = incidents,
    )
}

private fun getRiskLevelColor(riskLevel: String): Color =
    when (riskLevel) {
        "high" -> Color.Red
        "medium" -> Color.Yellow
        else -> Color.Blue
    }

@Composable
private fun createMapViewportState(location: Point): MapViewportState {
    return rememberMapViewportState {
        setCameraOptions {
            zoom(ZOOM_DEFAULT)
            center(location)
            pitch(PITCH)
            bearing(BEARING)
        }
    }
}

@Composable
private fun ConfigureMapBounds() {
    MapEffect(Unit) { mapView ->
        mapView.mapboxMap.setBounds(
            CameraBoundsOptions.Builder()
                .minZoom(ZOOM_MIN)
                .maxZoom(ZOOM_MAX)
                .build()
        )
    }
}

@Composable
private fun UserPosition(location: Point) {
    CircleAnnotation(location) {
        circleRadius = 16.0
        circleColor = Color(0x553498DB)
        circleStrokeWidth = 0.0
    }

    CircleAnnotation(location) {
        circleRadius = 8.0
        circleColor = Color.Cyan
        circleStrokeWidth = 2.0
        circleStrokeColor = Color.Black
    }
}

@Composable
private fun Map(
    mapViewportState: MapViewportState,
    incidents: List<Incident>,
    location: Point,
    isTrackUser: Boolean,
) {
    var incidentData by remember { mutableStateOf<Incident?>(null) }
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        scaleBar = { },
        compass = { /* TODO: Pindahkan compass ke bawah kanan */ },
        style = { MapStyle(Style.OUTDOORS) }
    ) {
        ConfigureMapBounds()

        if (isTrackUser) {
            UserPosition(location)
        }

        incidents.forEach { incident ->
            CircleAnnotation(Point.fromLngLat(incident.longitude, incident.latitude)) {
                circleRadius = 8.0
                circleColor = getRiskLevelColor(incident.riskLevel)
                circleStrokeWidth = 2.0
                circleStrokeColor = Color.Black

                interactionsState.onClicked {
                    incidentData = incident
                    isSheetOpen = true
                    true
                }
            }
        }

        if (isSheetOpen && incidentData != null) {
            BottomSheet(incidentData!!) { isSheetOpen = false }
        }
    }
}
