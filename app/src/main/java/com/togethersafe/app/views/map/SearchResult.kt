package com.togethersafe.app.views.map

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.togethersafe.app.constants.MapConstants.ZOOM_DEFAULT
import com.togethersafe.app.data.model.GeocodingLocation
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun SearchResult(locationResult: List<GeocodingLocation>) {
    if (locationResult.isEmpty()) NoSearchResult()
    else LocationList(locationResult)
}

@Composable
private fun NoSearchResult() {
    Box(
        modifier = Modifier
            .testTag("SearchNotFoundBox")
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
}

@Composable
private fun LocationList(locationResult: List<GeocodingLocation>) {
    LazyColumn(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .padding(10.dp)
            .heightIn(max = 400.dp)
            .wrapContentHeight()
            .pointerInput(Unit) { detectTapGestures {} }
    ) {
        itemsIndexed(locationResult) { index, geocodingLocation ->
            LocationItem(
                tag = "Item-$index",
                geocodingLocation = geocodingLocation,
            )
        }
    }
}

@Composable
private fun LocationItem(
    tag: String,
    geocodingLocation: GeocodingLocation,
) {
    val mapViewModel: MapViewModel = getViewModel()
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .testTag("Row-$tag")
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                handleLocationClick(
                    focusManager = focusManager,
                    mapViewModel = mapViewModel,
                    location = geocodingLocation,
                )
            }
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location Icon",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = geocodingLocation.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("Name-$tag"),
            )

            Text(
                text = geocodingLocation.display_name,
                fontSize = 12.sp, color = Color.Gray,
                modifier = Modifier.testTag("DisplayName-$tag"),
            )
        }
    }
}

fun handleLocationClick(
    focusManager: FocusManager,
    mapViewModel: MapViewModel,
    location: GeocodingLocation,
) {
    mapViewModel.setSearchedLocation(location)
    val locationPoint = location.getLocationPoint()
    mapViewModel.setZoomLevel(ZOOM_DEFAULT)
    mapViewModel.setCameraPosition(
        locationPoint.latitude(), locationPoint.longitude()
    )

    focusManager.clearFocus()
}
