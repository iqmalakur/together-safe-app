package com.togethersafe.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.togethersafe.app.data.model.GeocodingLocation
import com.togethersafe.app.ui.viewmodel.MapViewModel

@Composable
fun SearchResult(
    locationResult: List<GeocodingLocation>,
    focusManager: FocusManager,
) {
    if (locationResult.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 20.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Tidak Ada Hasil",
                textAlign = TextAlign.Center,
                color = Color.Gray,
            )
        }
    } else {
        LocationList(locationResult, focusManager)
    }
}

@Composable
private fun LocationList(
    locationResult: List<GeocodingLocation>,
    focusManager: FocusManager,
) {
    LazyColumn(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .padding(10.dp)
            .heightIn(max = 400.dp)
            .wrapContentHeight()
            .pointerInput(Unit) { detectTapGestures {} }
    ) {
        items(locationResult) { geocodingLocation ->
            LocationItem(geocodingLocation, focusManager)
        }
    }
}

@Composable
private fun LocationItem(
    geocodingLocation: GeocodingLocation,
    focusManager: FocusManager,
    mapViewModel: MapViewModel = hiltViewModel(),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val location = geocodingLocation.getLocationPoint()
                mapViewModel.setDestination(location)
                mapViewModel.setCameraPosition(
                    location.latitude(), location.longitude()
                )
                focusManager.clearFocus()
            }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location Icon",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = geocodingLocation.name, fontWeight = FontWeight.Bold)
            Text(text = geocodingLocation.display_name, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
