package com.togethersafe.app.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun UserPosition() {
    val mapViewModel: MapViewModel = getViewModel()

    val userPosition by mapViewModel.userPosition.collectAsState()
    val isLoadingLocation by mapViewModel.isLoadingLocation.collectAsState()
    val isTracking by mapViewModel.isTracking.collectAsState()

    LaunchedEffect(userPosition) {
        userPosition?.let {
            if (isTracking) mapViewModel.setCameraPosition(it.latitude(), it.longitude())
            if (isLoadingLocation) mapViewModel.setLoadingLocation(false)
        }
    }

    userPosition?.let {
        CircleAnnotation(it) {
            circleRadius = 16.0
            circleColor = Color(0x553498DB)
            circleStrokeWidth = 0.0
        }

        CircleAnnotation(it) {
            circleRadius = 8.0
            circleColor = Color.Cyan
            circleStrokeWidth = 2.0
            circleStrokeColor = Color.Black
        }
    }
}
