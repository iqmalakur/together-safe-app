package com.togethersafe.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.togethersafe.app.R
import com.togethersafe.app.constants.MapConstants.BEARING
import com.togethersafe.app.constants.MapConstants.PITCH
import com.togethersafe.app.constants.MapConstants.ZOOM_DEFAULT

@Composable
fun createMapViewportState(location: Point): MapViewportState {
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
fun DestinationAnnotation(destination: Point) {
    val markerIconDrawable = R.drawable.ic_marker
    val marker = rememberIconImage(
        key = markerIconDrawable,
        painter = painterResource(markerIconDrawable)
    )

    PointAnnotation(destination) {
        iconImage = marker
    }
}
