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
                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(106.8166, -6.2000)) // Jakarta
                        .zoom(10.0)
                        .build()
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}