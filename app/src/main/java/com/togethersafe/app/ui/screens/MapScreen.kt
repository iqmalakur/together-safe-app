package com.togethersafe.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.togethersafe.app.ui.components.BottomSheet
import com.togethersafe.app.utils.MapConfig.BEARING
import com.togethersafe.app.utils.MapConfig.PITCH
import com.togethersafe.app.utils.MapConfig.ZOOM_DEFAULT
import com.togethersafe.app.utils.MapConfig.ZOOM_MAX
import com.togethersafe.app.utils.MapConfig.ZOOM_MIN

@Composable
fun MapScreen(zoom: Double, showLocation: Boolean, resetShowLocation: () -> Unit) {
    val mapViewportState = createMapViewportState()
    val setCameraOptions: (CameraOptions.Builder) -> Unit = { cameraOptions ->
        mapViewportState.flyTo(cameraOptions.build())
    }

    LaunchedEffect(zoom) { setCameraOptions(CameraOptions.Builder().zoom(zoom)) }
    LaunchedEffect(showLocation) {
        if (showLocation) {
            setCameraOptions(
                CameraOptions.Builder().center(Point.fromLngLat(107.5420, -6.85))
            )
            resetShowLocation()
        }
    }

    Map(mapViewportState)
}

@Composable
private fun createMapViewportState(): MapViewportState {
    return rememberMapViewportState {
        setCameraOptions {
            zoom(ZOOM_DEFAULT)
            center(Point.fromLngLat(107.5420, -6.8789))
            pitch(PITCH)
            bearing(BEARING)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Map(mapViewportState: MapViewportState) {
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    var content by remember { mutableStateOf("") }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        scaleBar = { },
        compass = { }, // to be fix
        style = { MapStyle(Style.OUTDOORS) }
    ) {
        ConfigureMapBounds()

        CircleAnnotation(Point.fromLngLat(107.5420, -6.8789)) {
            circleRadius = 8.0
            circleColor = Color.Red
            circleStrokeWidth = 2.0
            circleStrokeColor = Color.Black

            interactionsState.onClicked {
                isSheetOpen = true
                content = "Marker 1"
                true
            }
        }

        CircleAnnotation(Point.fromLngLat(107.5420, -6.9)) {
            circleRadius = 8.0
            circleColor = Color.Red
            circleStrokeWidth = 2.0
            circleStrokeColor = Color.Black

            interactionsState.onClicked {
                isSheetOpen = true
                content = "Marker 2"
                true
            }
        }

        CircleAnnotation(Point.fromLngLat(107.5420, -6.7)) {
            circleRadius = 8.0
            circleColor = Color.Red
            circleStrokeWidth = 2.0
            circleStrokeColor = Color.Black

            interactionsState.onClicked {
                isSheetOpen = true
                content = "Marker 3"
                true
            }
        }

        if (isSheetOpen) {
            BottomSheet(content, sheetState) { isSheetOpen = false }
        }
    }
}

@Composable
private fun ConfigureMapBounds() {
    MapEffect(Unit) { mapView ->
        val mapboxMap = mapView.mapboxMap
        mapboxMap.setBounds(
            CameraBoundsOptions.Builder()
                .minZoom(ZOOM_MIN)
                .maxZoom(ZOOM_MAX)
                .build()
        )
    }
}
