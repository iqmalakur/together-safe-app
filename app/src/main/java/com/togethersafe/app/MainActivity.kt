package com.togethersafe.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapScreen()
        }
    }
}

@Composable
fun MapScreen() {
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(15.0)
            center(Point.fromLngLat(107.5420, -6.8789))
            pitch(0.0)
            bearing(0.0)
        }
    }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        style = { MapStyle(Style.OUTDOORS) }
    ) {
        MapEffect(Unit) { mapView ->
            val mapboxMap = mapView.mapboxMap
            val cameraBoundsOption = CameraBoundsOptions.Builder()
                .minZoom(5.0)
                .maxZoom(20.0)
                .build()

            mapboxMap.setBounds(cameraBoundsOption)
        }

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
