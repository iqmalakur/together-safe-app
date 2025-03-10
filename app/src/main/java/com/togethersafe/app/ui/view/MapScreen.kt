package com.togethersafe.app.ui.view

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
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
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.OnScaleListener
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import com.mapbox.maps.plugin.gestures.addOnScaleListener
import com.togethersafe.app.R
import com.togethersafe.app.ui.components.BottomSheet
import com.togethersafe.app.ui.components.MapButtons
import com.togethersafe.app.ui.viewmodel.AppViewModel
import com.togethersafe.app.ui.viewmodel.IncidentViewModel
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
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val isTracking by mapViewModel.isTracking.collectAsState()
    var isLocationPermissionGranted by remember { mutableStateOf(false) }

    if (isTracking) {
        appViewModel.requestPermission {
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
private fun Destination(mapViewModel: MapViewModel = hiltViewModel()) {
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
private fun IncidentMarkers(incidentViewModel: IncidentViewModel = hiltViewModel()) {
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

@Composable
private fun Map(
    mapViewportState: MapViewportState,
    mapViewModel: MapViewModel = hiltViewModel(),
) {
    val userPosition by mapViewModel.userPosition.collectAsState()
    val destination by mapViewModel.destination.collectAsState()
    val focusManager = LocalFocusManager.current

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        onMapClickListener = {
            focusManager.clearFocus()
            false
        },
        scaleBar = { },
        compass = {
            MapButtons {
                Compass(
                    fadeWhenFacingNorth = false,
                    alignment = Alignment.Center,
                )
            }
        },
        style = { MapStyle(Style.OUTDOORS) }
    ) {
        MapEffect(Unit) { mapView ->
            configureMapBounds(mapView)
            handleOnMove(mapView, mapViewModel)
            handleOnScale(mapView, mapViewModel)
        }

        if (userPosition != null) UserPosition()
        if (destination != null) Destination()
        IncidentMarkers()
    }
}
