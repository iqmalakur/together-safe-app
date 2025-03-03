package com.togethersafe.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.togethersafe.app.ui.viewmodel.LocationViewModel
import com.togethersafe.app.ui.viewmodel.MapViewModel

@Composable
fun RoundedIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .padding(5.dp)
            .clip(CircleShape)
            .background(Color.White),
        onClick = onClick
    ) {
        Icon(imageVector = imageVector, contentDescription = contentDescription)
    }
}

@Composable
fun MapButtons(
    mapViewModel: MapViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel(),
) {
    val buttons = listOf(
        Triple(Icons.Rounded.MyLocation, "Lokasi Saya", locationViewModel::startLocating),
        Triple(Icons.Rounded.ZoomIn, "Perbesar Peta", mapViewModel::zoomIn),
        Triple(Icons.Rounded.ZoomOut, "Perkecil Peta", mapViewModel::zoomOut),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
        buttons.forEach { (icon, description, action) ->
            RoundedIconButton(
                imageVector = icon,
                contentDescription = description,
                onClick = action
            )
        }
    }
}
