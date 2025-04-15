package com.togethersafe.app.views.report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import com.togethersafe.app.utils.DestinationAnnotation
import com.togethersafe.app.utils.createMapViewportState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LocationPicker(
    value: Point,
    enableScroll: () -> Unit,
    disableScroll: () -> Unit,
    onChange: (location: Point) -> Unit,
) {
    val mapViewportState = createMapViewportState(value)

    Text(
        text = "Lokasi Insiden",
        fontSize = 16.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .pointerInteropFilter {
                disableScroll()
                false
            }
    ) {
        MapboxMap(
            mapViewportState = mapViewportState,
            scaleBar = {},
            compass = {},
            attribution = {},
            logo = {},
            style = { MapStyle(Style.OUTDOORS) },
        ) {
            MapEffect(Unit) { mapView ->
                setupMapBounds(mapView)
                handleOnMoveListener(
                    mapView = mapView,
                    onMove = onChange,
                    onMoveEnd = enableScroll,
                )
            }

            DestinationAnnotation(value)
        }
    }
}

private fun setupMapBounds(mapView: MapView) {
    mapView.mapboxMap.setBounds(
        CameraBoundsOptions.Builder()
            .minZoom(10.0)
            .maxZoom(20.0)
            .build()
    )
}

private fun handleOnMoveListener(
    mapView: MapView,
    onMove: (Point) -> Unit,
    onMoveEnd: () -> Unit,
) {
    mapView.mapboxMap.addOnMoveListener(
        object : OnMoveListener {
            override fun onMove(detector: MoveGestureDetector): Boolean {
                val center = mapView.mapboxMap.cameraState.center
                onMove(center)
                return false
            }

            override fun onMoveBegin(detector: MoveGestureDetector) {}

            override fun onMoveEnd(detector: MoveGestureDetector) {
                onMoveEnd()
            }
        }
    )
}
