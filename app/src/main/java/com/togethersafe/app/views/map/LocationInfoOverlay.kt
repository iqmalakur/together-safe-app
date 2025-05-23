package com.togethersafe.app.views.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.GeolocationViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun BoxScope.LocationInfoOverlay() {
    val mapViewModel: MapViewModel = getViewModel()
    val geolocationViewModel: GeolocationViewModel = getViewModel()

    val searchedLocation by mapViewModel.searchedLocation.collectAsState()

    searchedLocation?.let { location ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.BottomCenter)
        ) {
            CardBackground {
                Column {
                    HeaderSection(
                        name = location.name,
                        address = location.fullName,
                        onClose = {
                            mapViewModel.setSearchedLocation(null)
                            geolocationViewModel.clearRoutes()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ActionButtons()
                }
            }
        }
    }
}

@Composable
private fun CardBackground(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                clip = false
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(16.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun HeaderSection(
    name: String,
    address: String,
    onClose: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Tutup"
            )
        }
    }
}

@Composable
private fun ActionButtons() {
    val appViewModel: AppViewModel = getViewModel()
    val mapViewModel: MapViewModel = getViewModel()
    val geolocationViewModel: GeolocationViewModel = getViewModel()

    val userLocation by mapViewModel.userPosition.collectAsState()
    val searchedLocation by mapViewModel.searchedLocation.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val destination =
                        Point.fromLngLat(searchedLocation!!.longitude, searchedLocation!!.latitude)

                    appViewModel.setLoading(true)
                    geolocationViewModel.fetchSafeRoutes(userLocation!!, destination, {
                        appViewModel.setToastMessage(it)
                    }) { appViewModel.setLoading(false) }
                },
                enabled = userLocation != null && searchedLocation != null,
                modifier = Modifier.weight(1f),
            ) {
                Text("Cari Rute Aman")
            }
        }

        if (userLocation == null || searchedLocation == null) {
            Text(
                text = "Lokasi pengguna dan destinasi harus tersedia untuk mencari rute.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
