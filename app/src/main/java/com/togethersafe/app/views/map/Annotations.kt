package com.togethersafe.app.views.map

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
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.withFillColor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolygonAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.togethersafe.app.constants.MapConstants.EARTH_RADIUS_METERS
import com.togethersafe.app.utils.DestinationAnnotation
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.GeolocationViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.viewmodels.MapViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Annotations() {
    val mapViewModel: MapViewModel = getViewModel()
    val geolocationViewModel: GeolocationViewModel = getViewModel()

    val userPosition by mapViewModel.userPosition.collectAsState()
    val searchedLocation by mapViewModel.searchedLocation.collectAsState()
    val routes by geolocationViewModel.routes.collectAsState()

    if (userPosition != null) UserPosition(mapViewModel)
    if (searchedLocation != null) {
        val point = Point.fromLngLat(searchedLocation!!.longitude, searchedLocation!!.latitude)
        DestinationAnnotation(point)
    }
    IncidentMarkers()

    if (routes.isNotEmpty()) {
        routes.forEach {
            PolylineAnnotation(it) {
                lineColor = Color.Red
                lineWidth = 5.0
            }
        }
    }
}

@Composable
private fun UserPosition(mapViewModel: MapViewModel) {
    val userPosition by mapViewModel.userPosition.collectAsState()

    CircleAnnotation(userPosition!!) {
        circleRadius = 16.0
        circleColor = Color(0x553498DB)
        circleStrokeWidth = 0.0
    }

    CircleAnnotation(userPosition!!) {
        circleRadius = 8.0
        circleColor = Color.Cyan
        circleStrokeWidth = 2.0
        circleStrokeColor = Color.Black
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IncidentMarkers() {
    val appViewModel: AppViewModel = getViewModel()
    val incidentViewModel: IncidentViewModel = getViewModel()
    val incidents by incidentViewModel.incidents.collectAsState()
    val selectedIncident by incidentViewModel.selectedIncident.collectAsState()
    var polygonManager by remember { mutableStateOf<PolygonAnnotationManager?>(null) }
    val sheetState = rememberModalBottomSheetState()

    MapEffect(incidents) { mapView ->
        val mapboxMap = mapView.mapboxMap

        when (polygonManager) {
            null -> polygonManager = mapView.annotations.createPolygonAnnotationManager()
            else -> polygonManager!!.deleteAll()
        }

        val polygons = incidents.map { incident ->
            val center = Point.fromLngLat(incident.longitude, incident.latitude)
            val circlePoints = createCircleCoordinates(center, incident.radius)

            val polygon = polygonManager!!.create(
                PolygonAnnotationOptions()
                    .withPoints(listOf(circlePoints))
                    .withFillColor(getRiskLevelColor(incident.riskLevel))
                    .withFillOpacity(0.3)
            )

            Pair(polygon, incident)
        }

        mapView.gestures.addOnMapClickListener { point ->
            val clickedLocation = mapboxMap.pixelForCoordinate(point)
            val tappedLatLng = mapboxMap.coordinateForPixel(clickedLocation)

            polygons.forEach { (polygon, incident) ->
                val polygonPoints = polygon.points.firstOrNull() ?: return@forEach
                if (isPointInPolygon(tappedLatLng, polygonPoints)) {
                    appViewModel.setLoading(true)

                    incidentViewModel.fetchIncidentById(
                        id = incident.id,
                        onError = { _, errors -> appViewModel.setToastMessage(errors[0]) })
                    { appViewModel.setLoading(false) }

                    return@addOnMapClickListener true
                }
            }

            false
        }
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
