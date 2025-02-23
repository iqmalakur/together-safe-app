package com.togethersafe.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
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
import com.togethersafe.app.data.Incident
import com.togethersafe.app.ui.components.BottomSheet
import com.togethersafe.app.utils.MapConfig.BEARING
import com.togethersafe.app.utils.MapConfig.LATITUDE_DEFAULT
import com.togethersafe.app.utils.MapConfig.LONGITUDE_DEFAULT
import com.togethersafe.app.utils.MapConfig.PITCH
import com.togethersafe.app.utils.MapConfig.ZOOM_DEFAULT
import com.togethersafe.app.utils.MapConfig.ZOOM_MAX
import com.togethersafe.app.utils.MapConfig.ZOOM_MIN

@Composable
fun MapScreen(zoom: Double, showLocation: Boolean, resetShowLocation: () -> Unit) {
    val location = Point.fromLngLat(LONGITUDE_DEFAULT, LATITUDE_DEFAULT)
    val mapViewportState = createMapViewportState(location)
    val setCameraOptions: (CameraOptions.Builder) -> Unit = { cameraOptions ->
        mapViewportState.flyTo(cameraOptions.build())
    }

    LaunchedEffect(zoom) { setCameraOptions(CameraOptions.Builder().zoom(zoom)) }
    LaunchedEffect(showLocation) {
        if (showLocation) {
            setCameraOptions(CameraOptions.Builder().center(location))
            resetShowLocation()
        }
    }

    Map(mapViewportState)
}

private fun getRiskLevelColor(riskLevel: String): Color =
    when (riskLevel) {
        "Tinggi" -> Color.Red
        "Sedang" -> Color.Yellow
        else -> Color.Blue
    }

@Composable
private fun createMapViewportState(location: Point): MapViewportState {
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
private fun ConfigureMapBounds() {
    MapEffect(Unit) { mapView ->
        mapView.mapboxMap.setBounds(
            CameraBoundsOptions.Builder()
                .minZoom(ZOOM_MIN)
                .maxZoom(ZOOM_MAX)
                .build()
        )
    }
}

@Composable
private fun Map(mapViewportState: MapViewportState) {
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    var incident: Incident? by remember { mutableStateOf(null) }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        scaleBar = { },
        compass = { /* TODO: Pindahkan compass ke bawah kanan */ },
        style = { MapStyle(Style.OUTDOORS) }
    ) {
        ConfigureMapBounds()

        CircleAnnotation(Point.fromLngLat(107.5420, -6.8789)) {
            incident = Incident(
                category = "Pembegalan",
                riskLevel = "Tinggi",
                description = "Terjadi pembegalan di daerah Jalan Raya Cimahi pada malam hari. Pelaku menggunakan motor dan membawa senjata tajam.",
                location = "Jalan Raya Cimahi",
                dateTime = "17 Februari 2025, 22:30 WIB",
                status = "Terverifikasi",
                reportCount = 5,
                reports = listOf(
                    "Saya melihat kejadian ini sekitar pukul 22:15 WIB.",
                    "Pelaku membawa senjata tajam dan merampas motor korban.",
                    "Korban mengalami luka ringan dan sudah melapor ke polisi.",
                    "Pelaku kabur ke arah jalan tol setelah kejadian.",
                    "Beberapa warga mencoba mengejar pelaku tetapi gagal."
                ),
                mediaUrls = listOf(
                    "https://source.unsplash.com/300x200/?crime,street",
                    "https://source.unsplash.com/300x200/?accident,night",
                    "https://source.unsplash.com/300x200/?police,incident"
                ),
            )

            circleRadius = 8.0
            circleColor = getRiskLevelColor(incident!!.riskLevel)
            circleStrokeWidth = 2.0
            circleStrokeColor = Color.Black

            interactionsState.onClicked {
                isSheetOpen = true
                true
            }
        }

        CircleAnnotation(Point.fromLngLat(107.5420, -6.9)) {
            incident = Incident(
                category = "Pencurian",
                riskLevel = "Tinggi",
                description = "Terjadi pencurian mobil",
                location = "Jalan Raya Cimahi",
                dateTime = "18 Februari 2025, 08:00 WIB",
                status = "Terverifikasi",
                reportCount = 5,
                reports = listOf(
                    "Saya melihat kejadian ini sekitar pukul 22:15 WIB.",
                    "Pelaku membawa senjata tajam dan merampas motor korban.",
                    "Korban mengalami luka ringan dan sudah melapor ke polisi.",
                    "Pelaku kabur ke arah jalan tol setelah kejadian.",
                    "Beberapa warga mencoba mengejar pelaku tetapi gagal."
                ),
                mediaUrls = listOf(
                    "https://source.unsplash.com/300x200/?crime,street",
                    "https://source.unsplash.com/300x200/?accident,night",
                    "https://source.unsplash.com/300x200/?police,incident"
                ),
            )

            circleRadius = 8.0
            circleColor = getRiskLevelColor(incident!!.riskLevel)
            circleStrokeWidth = 2.0
            circleStrokeColor = Color.Black

            interactionsState.onClicked {
                isSheetOpen = true
                true
            }
        }

        CircleAnnotation(Point.fromLngLat(107.5420, -6.7)) {
            incident = Incident(
                category = "Kecelakaan",
                riskLevel = "Sedang",
                description = "Terjadi kecelakaan beruntun",
                location = "Jalan Raya Cimahi",
                dateTime = "15 Februari 2025, 12:00 WIB",
                status = "Terverifikasi",
                reportCount = 5,
                reports = listOf(
                    "Saya melihat kejadian ini sekitar pukul 22:15 WIB.",
                    "Pelaku membawa senjata tajam dan merampas motor korban.",
                    "Korban mengalami luka ringan dan sudah melapor ke polisi.",
                    "Pelaku kabur ke arah jalan tol setelah kejadian.",
                    "Beberapa warga mencoba mengejar pelaku tetapi gagal."
                ),
                mediaUrls = listOf(
                    "https://source.unsplash.com/300x200/?crime,street",
                    "https://source.unsplash.com/300x200/?accident,night",
                    "https://source.unsplash.com/300x200/?police,incident"
                ),
            )

            circleRadius = 8.0
            circleColor = getRiskLevelColor(incident!!.riskLevel)
            circleStrokeWidth = 2.0
            circleStrokeColor = Color.Black

            interactionsState.onClicked {
                isSheetOpen = true
                true
            }
        }

        if (isSheetOpen && incident != null) {
            BottomSheet(incident!!) { isSheetOpen = false }
        }
    }
}
