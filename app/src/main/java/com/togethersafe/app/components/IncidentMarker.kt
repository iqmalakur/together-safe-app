package com.togethersafe.app.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.annotation.generated.withFillColor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolygonAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener
import com.togethersafe.app.constants.MapConstants.EARTH_RADIUS_METERS
import com.togethersafe.app.utils.getFormattedIncidentRisk
import com.togethersafe.app.utils.getFormattedIncidentStatus
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.views.map.BottomSheet
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentMarkers() {
    val appViewModel: AppViewModel = getViewModel()
    val incidentViewModel: IncidentViewModel = getViewModel()

    val incidents by incidentViewModel.incidents.collectAsState()
    val selectedIncident by incidentViewModel.selectedIncident.collectAsState()
    val incidentFilter by appViewModel.incidentFilter.collectAsState()

    var polygonManager by remember { mutableStateOf<PolygonAnnotationManager?>(null) }
    var mapClickListener by remember { mutableStateOf(OnMapClickListener { false }) }
    val sheetState = rememberModalBottomSheetState()

    MapEffect(incidents, incidentFilter) { mapView ->
        val mapboxMap = mapView.mapboxMap

        when (polygonManager) {
            null -> polygonManager = mapView.annotations.createPolygonAnnotationManager()
            else -> polygonManager!!.deleteAll()
        }

        val polygons = incidents.mapNotNull { incident ->
            if (incidentFilter[getFormattedIncidentRisk(incident.riskLevel)] == false) null
            else if (incidentFilter[getFormattedIncidentStatus(incident.status)] == false) null
            else {
                val center = Point.fromLngLat(incident.longitude, incident.latitude)
                val circlePoints = createCircleCoordinates(center, incident.radius)

                val polygon = polygonManager!!.create(
                    PolygonAnnotationOptions()
                        .withPoints(listOf(circlePoints))
                        .withFillColor(
                            if (incident.status == "pending") Color.Gray
                            else getRiskLevelColor(incident.riskLevel)
                        )
                        .withFillOpacity(0.3)
                )

                Pair(polygon, incident)
            }
        }

        mapboxMap.removeOnMapClickListener(mapClickListener)
        mapClickListener = OnMapClickListener { point ->
            val clickedLocation = mapboxMap.pixelForCoordinate(point)
            val tappedLatLng = mapboxMap.coordinateForPixel(clickedLocation)

            polygons.forEach { (polygon, incident) ->
                val polygonPoints = polygon.points.firstOrNull() ?: return@forEach
                if (isPointInPolygon(tappedLatLng, polygonPoints)) {
                    appViewModel.setLoading(true)

                    incidentViewModel.fetchIncidentById(
                        id = incident.id,
                        onError = { _, errors -> appViewModel.setToastMessage(errors[0]) }
                    ) { appViewModel.setLoading(false) }

                    return@OnMapClickListener true
                }
            }

            false
        }
        mapboxMap.addOnMapClickListener(mapClickListener)
    }

    if (selectedIncident != null) {
        BottomSheet(sheetState)
    }
}

fun createCircleCoordinates(center: Point, radiusInMeters: Double, steps: Int = 64): List<Point> {
    val coordinates = mutableListOf<Point>()

    for (i in 0 until steps) {
        val angle = 2.0 * Math.PI * i / steps
        val dx = radiusInMeters * cos(angle)
        val dy = radiusInMeters * sin(angle)

        val latitude = center.latitude() + (dy / EARTH_RADIUS_METERS) * (180 / Math.PI)
        val longitude = center.longitude() +
                (dx / (EARTH_RADIUS_METERS * cos(center.latitude() * Math.PI / 180))) *
                (180 / Math.PI)

        coordinates.add(Point.fromLngLat(longitude, latitude))
    }

    coordinates.add(coordinates.first())
    return coordinates
}

fun isPointInPolygon(point: Point, polygon: List<Point>): Boolean {
    val x = point.longitude()
    val y = point.latitude()
    var inside = false

    for (i in polygon.indices) {
        val j = (i - 1 + polygon.size) % polygon.size
        val xi = polygon[i].longitude()
        val yi = polygon[i].latitude()
        val xj = polygon[j].longitude()
        val yj = polygon[j].latitude()

        val intersect = ((yi > y) != (yj > y)) &&
                (x < (xj - xi) * (y - yi) / (yj - yi + 0.000000001) + xi)
        if (intersect) inside = !inside
    }

    return inside
}

private fun getRiskLevelColor(riskLevel: String): Color =
    when (riskLevel) {
        "high" -> Color.Red
        "medium" -> Color.Yellow
        else -> Color.Blue
    }
