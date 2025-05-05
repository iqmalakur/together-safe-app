package com.togethersafe.app.views.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import com.togethersafe.app.components.RoundedIconButton
import com.togethersafe.app.components.ZoomButton
import com.togethersafe.app.constants.MapConstants.ZOOM_DEFAULT
import com.togethersafe.app.constants.MapConstants.ZOOM_STEP
import com.togethersafe.app.utils.DestinationAnnotation
import com.togethersafe.app.utils.createMapViewportState
import com.togethersafe.app.utils.handleOnScale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LocationPicker(
    value: Point,
    enableScroll: () -> Unit,
    disableScroll: () -> Unit,
    onChange: (location: Point) -> Unit,
) {
    val mapViewportState = createMapViewportState(value)
    var selectedLocation by remember { mutableStateOf(value) }
    var zoomLevel by remember { mutableDoubleStateOf(ZOOM_DEFAULT) }
    var isExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedLocation) {
        onChange(selectedLocation)
    }

    LaunchedEffect(zoomLevel) {
        mapViewportState.flyTo(
            CameraOptions.Builder()
                .zoom(zoomLevel)
                .build()
        )
    }

    Text(
        text = "Lokasi Insiden",
        fontSize = 16.sp,
        modifier = Modifier.padding(top = 4.dp)
    )

    Text(
        text = "(tekan lama pada peta untuk memilih lokasi)",
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                when (isExpanded) {
                    true -> 500.dp
                    false -> 200.dp
                }
            )
            .pointerInteropFilter {
                disableScroll()
                false
            }
    ) {
        MapboxMap(
            mapViewportState = mapViewportState,
            onMapLongClickListener = {
                selectedLocation = it
                enableScroll()
                false
            },
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
                    onMoveEnd = enableScroll,
                )
                handleOnScale(mapView) { zoomLevel = it }
            }

            DestinationAnnotation(selectedLocation)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
        ) {
            ZoomButton(
                onZoomIn = {
                    if (zoomLevel < 20.0) zoomLevel += ZOOM_STEP
                    enableScroll()
                },
                onZoomOut = {
                    if (zoomLevel > 10.0) zoomLevel -= ZOOM_STEP
                    enableScroll()
                },
            )

            if (!isExpanded) {
                RoundedIconButton(
                    imageVector = Icons.Rounded.Fullscreen,
                    contentDescription = "Luaskan Peta",
                    onClick = {
                        isExpanded = true
                        enableScroll()
                    }
                )
            } else {
                RoundedIconButton(
                    imageVector = Icons.Rounded.FullscreenExit,
                    contentDescription = "Kecilkan Peta",
                    onClick = {
                        isExpanded = false
                        enableScroll()
                    }
                )
            }
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
    onMoveEnd: () -> Unit,
) {
    mapView.mapboxMap.addOnMoveListener(
        object : OnMoveListener {
            override fun onMove(detector: MoveGestureDetector) = false
            override fun onMoveBegin(detector: MoveGestureDetector) {}

            override fun onMoveEnd(detector: MoveGestureDetector) {
                onMoveEnd()
            }
        }
    )
}
