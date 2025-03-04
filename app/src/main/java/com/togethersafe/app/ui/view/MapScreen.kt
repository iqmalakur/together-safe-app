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
import androidx.hilt.navigation.compose.hiltViewModel
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.android.gestures.StandardScaleGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.OnScaleListener
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import com.mapbox.maps.plugin.gestures.addOnScaleListener
import com.togethersafe.app.data.model.Incident
import com.togethersafe.app.ui.components.BottomSheet
import com.togethersafe.app.ui.viewmodel.IncidentViewModel
import com.togethersafe.app.ui.viewmodel.PermissionViewModel
import com.togethersafe.app.ui.viewmodel.MapViewModel
import com.togethersafe.app.utils.GetUserLocation
import com.togethersafe.app.utils.MapConfig.BEARING
import com.togethersafe.app.utils.MapConfig.PITCH
import com.togethersafe.app.utils.MapConfig.ZOOM_DEFAULT
import com.togethersafe.app.utils.MapConfig.ZOOM_MAX
import com.togethersafe.app.utils.MapConfig.ZOOM_MIN

@Composable
fun MapScreen(
    context: Context,
    mapViewModel: MapViewModel = hiltViewModel(),
) {
    val cameraPosition by mapViewModel.cameraPosition.collectAsState()
    val zoomLevel by mapViewModel.zoomLevel.collectAsState()
    val mapViewportState = createMapViewportState(cameraPosition)

    LaunchedEffect(cameraPosition, zoomLevel) {
        mapViewportState.flyTo(
            CameraOptions.Builder()
                .center(cameraPosition)
                .zoom(zoomLevel)
                .build()
        )
    }

    Map(mapViewportState)
    Tracking(context)
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
private fun Tracking(
    context: Context,
    mapViewModel: MapViewModel = hiltViewModel(),
    permissionViewModel: PermissionViewModel = hiltViewModel(),
) {
    val isTracking by mapViewModel.isTracking.collectAsState()
    var isLocationPermissionGranted by remember { mutableStateOf(false) }

    if (isTracking) {
        permissionViewModel.requestPermission {
            isLocationPermissionGranted = true
        }

        if (isLocationPermissionGranted) {
            GetUserLocation(context)
        }
    }
}

private fun configureMapBounds(mapView: MapView) {
    mapView.mapboxMap.setBounds(
        CameraBoundsOptions.Builder()
            .minZoom(ZOOM_MIN)
            .maxZoom(ZOOM_MAX)
            .build()
    )
}

private fun handleOnMove(mapView: MapView, mapViewModel: MapViewModel) {
    mapView.mapboxMap.addOnMoveListener(
        object : OnMoveListener {
            override fun onMove(detector: MoveGestureDetector) = false

            override fun onMoveBegin(detector: MoveGestureDetector) {
                mapViewModel.stopTracking()
            }

            override fun onMoveEnd(detector: MoveGestureDetector) {
                val center = mapView.mapboxMap.cameraState.center
                mapViewModel.setCameraPosition(center.latitude(), center.longitude())
            }
        }
    )
}

private fun handleOnScale(mapView: MapView, mapViewModel: MapViewModel) {
    mapView.mapboxMap.addOnScaleListener(
        object : OnScaleListener {
            override fun onScale(detector: StandardScaleGestureDetector) {}
            override fun onScaleBegin(detector: StandardScaleGestureDetector) {}

            override fun onScaleEnd(detector: StandardScaleGestureDetector) {
                mapViewModel.setZoomLevel(mapView.mapboxMap.cameraState.zoom)
            }
        }
    )
}

@Composable
private fun UserPosition(mapViewModel: MapViewModel = hiltViewModel()) {
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
private fun Map(
    mapViewportState: MapViewportState,
    incidentViewModel: IncidentViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
) {
    val incidents by incidentViewModel.incidents.collectAsState()
    val userPosition by mapViewModel.userPosition.collectAsState()
    var incidentData by remember { mutableStateOf<Incident?>(null) }
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        scaleBar = { },
        compass = { /* TODO: Pindahkan compass ke bawah kanan */ },
        style = { MapStyle(Style.OUTDOORS) }
    ) {
        MapEffect(Unit) { mapView ->
            configureMapBounds(mapView)
            handleOnMove(mapView, mapViewModel)
            handleOnScale(mapView, mapViewModel)
        }

        if (userPosition != null) UserPosition()
        LaunchedEffect(Unit) { incidentViewModel.loadIncidents() }

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
