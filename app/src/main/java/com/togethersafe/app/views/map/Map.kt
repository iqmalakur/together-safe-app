package com.togethersafe.app.views.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import com.togethersafe.app.constants.MapConstants.ZOOM_MAX
import com.togethersafe.app.constants.MapConstants.ZOOM_MIN
import com.togethersafe.app.data.dto.GeocodingResDto
import com.togethersafe.app.utils.GetUserLocation
import com.togethersafe.app.utils.createMapViewportState
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.utils.handleOnScale
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.GeolocationViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun Map() {
    val appViewModel: AppViewModel = getViewModel()
    val mapViewModel: MapViewModel = getViewModel()
    val geolocationViewModel: GeolocationViewModel = getViewModel()

    val cameraPosition by mapViewModel.cameraPosition.collectAsState()
    val zoomLevel by mapViewModel.zoomLevel.collectAsState()
    val focusManager = LocalFocusManager.current
    val mapViewportState = createMapViewportState(cameraPosition)

    LaunchedEffect(cameraPosition, zoomLevel) {
        mapViewportState.flyTo(
            CameraOptions.Builder()
                .center(cameraPosition)
                .zoom(zoomLevel)
                .build()
        )
    }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        onMapClickListener = {
            focusManager.clearFocus()
            false
        },
        onMapLongClickListener = {
            appViewModel.setLoading(true)
            geolocationViewModel.fetchLocation(
                latitude = it.latitude(),
                longitude = it.longitude(),
                onError = { error -> appViewModel.setToastMessage(error) },
                onComplete = { appViewModel.setLoading(false) },
                onSuccess = { location ->
                    mapViewModel.setSearchedLocation(
                        GeocodingResDto(
                            latitude = it.latitude(),
                            longitude = it.longitude(),
                            name = location.name,
                            fullName = location.fullName,
                        )
                    )
                }
            )
            false
        },
        scaleBar = { },
        compass = {
            ActionButton {
                Compass(
                    fadeWhenFacingNorth = false,
                    alignment = Alignment.Center,
                )
            }
        },
        style = { MapStyle(Style.OUTDOORS) }
    ) {
        MapSetup(mapViewModel)
        Annotations()
    }

    Tracking(mapViewModel)
}

@Composable
private fun MapSetup(mapViewModel: MapViewModel) {
    MapEffect(Unit) { mapView ->
        configureMapBounds(mapView)
        handleOnMove(mapView, mapViewModel)
        handleOnScale(mapView) { mapViewModel.setZoomLevel(it) }
    }
}

@Composable
private fun Tracking(mapViewModel: MapViewModel) {
    val appViewModel: AppViewModel = getViewModel()
    val isTracking by mapViewModel.isTracking.collectAsState()
    var isLocationPermissionGranted by remember { mutableStateOf(false) }

    if (isTracking) {
        appViewModel.requestPermission {
            isLocationPermissionGranted = true
        }

        if (isLocationPermissionGranted) {
            GetUserLocation()
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
                mapViewModel.cancelScheduledMapSave()
            }

            override fun onMoveEnd(detector: MoveGestureDetector) {
                val center = mapView.mapboxMap.cameraState.center
                mapViewModel.setCameraPosition(center.latitude(), center.longitude())
                mapViewModel.scheduleMapSave(center)
            }
        }
    )
}
