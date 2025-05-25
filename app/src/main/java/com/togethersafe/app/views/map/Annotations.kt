package com.togethersafe.app.views.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.togethersafe.app.components.IncidentMarkers
import com.togethersafe.app.components.UserPosition
import com.togethersafe.app.utils.DestinationAnnotation
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.GeolocationViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun Annotations() {
    val mapViewModel: MapViewModel = getViewModel()
    val searchedLocation by mapViewModel.searchedLocation.collectAsState()

    if (searchedLocation != null) {
        val point = Point.fromLngLat(searchedLocation!!.longitude, searchedLocation!!.latitude)
        DestinationAnnotation(point)
    }

    UserPosition()
    IncidentMarkers()
    DrawRoutes()
}

@Composable
private fun DrawRoutes() {
    val geolocationViewModel: GeolocationViewModel = getViewModel()
    val routes by geolocationViewModel.routes.collectAsState()

    if (routes.isNotEmpty()) {
        routes.forEach {
            PolylineAnnotation(it) {
                lineColor = Color.Red
                lineWidth = 5.0
            }
        }
    }
}
