package com.togethersafe.app.utils

import com.mapbox.android.gestures.StandardScaleGestureDetector
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.OnScaleListener
import com.mapbox.maps.plugin.gestures.addOnScaleListener

fun handleOnScale(mapView: MapView, onScaleEnd: (zoom: Double) -> Unit) {
    mapView.mapboxMap.addOnScaleListener(
        object : OnScaleListener {
            override fun onScale(detector: StandardScaleGestureDetector) {}
            override fun onScaleBegin(detector: StandardScaleGestureDetector) {}

            override fun onScaleEnd(detector: StandardScaleGestureDetector) {
                onScaleEnd(mapView.mapboxMap.cameraState.zoom)
            }
        }
    )
}
