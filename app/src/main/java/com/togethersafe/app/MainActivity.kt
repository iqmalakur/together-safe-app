package com.togethersafe.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.maps.MapView
import com.mapbox.maps.CameraOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.Style

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapScreen()
        }
    }
}

@Composable
fun MapScreen() {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                mapboxMap.loadStyle(Style.TRAFFIC_DAY)

                mapboxMap.setBounds(
                    CameraBoundsOptions.Builder()
                        .minZoom(5.0)
                        .maxZoom(20.0)
                        .build()
                )

                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(107.5420, -6.8789)) // Jakarta
                        .zoom(15.0)
                        .build()
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}