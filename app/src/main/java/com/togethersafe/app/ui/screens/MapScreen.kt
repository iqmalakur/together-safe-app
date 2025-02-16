package com.togethersafe.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
private fun Map(mapViewportState: MapViewportState) {
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        style = { MapStyle(Style.OUTDOORS) }
    ) {
        ConfigureMapBounds()

        CircleAnnotation(Point.fromLngLat(107.5420, -6.8789)) {
            circleRadius = 8.0
            circleColor = Color.Red
            circleStrokeWidth = 2.0
            circleStrokeColor = Color.Black

            interactionsState.onClicked {
                circleColor = if (circleColor == Color.Red) Color.Blue else Color.Red
                true
            }
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
