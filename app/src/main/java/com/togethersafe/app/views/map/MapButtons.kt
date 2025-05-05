package com.togethersafe.app.views.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.LocationSearching
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.togethersafe.app.components.RoundedIconButton
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.utils.isLocationEnabled
import com.togethersafe.app.utils.promptEnableGPS
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun ActionButton(compass: @Composable () -> Unit) {
    val appViewModel: AppViewModel = getViewModel()
    val incidentViewModel: IncidentViewModel = getViewModel()
    val mapViewModel: MapViewModel = getViewModel()

    val isLoadingLocation by mapViewModel.isLoadingLocation.collectAsState()
    val isLoadingIncident by incidentViewModel.isLoadingIncident.collectAsState()
    val isTracking by mapViewModel.isTracking.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
        RoundedIconButton(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = "Refresh Incident",
            loadingState = isLoadingIncident,
            onClick = {
                val cameraPosition = mapViewModel.cameraPosition.value
                incidentViewModel.loadIncidents(cameraPosition) { _, errors ->
                    appViewModel.setToastMessage(errors[0])
                }
            }
        )

        RoundedIconButton(
            imageVector = Icons.Rounded.ZoomIn,
            contentDescription = "Perbesar Peta",
            onClick = { mapViewModel.zoomIn() }
        )

        RoundedIconButton(
            imageVector = Icons.Rounded.ZoomOut,
            contentDescription = "Perkecil Peta",
            onClick = { mapViewModel.zoomOut() }
        )

        RoundedIconButton(
            modifier = Modifier.testTag("UserTrackButton"),
            imageVector =
            if (isTracking) Icons.Rounded.MyLocation
            else Icons.Rounded.LocationSearching,
            contentDescription = "Lokasi Saya",
            loadingState = isLoadingLocation,
            onClick = {
                if (!isTracking) {
                    if (!isLocationEnabled(context)) {
                        promptEnableGPS(context, appViewModel)
                    } else {
                        mapViewModel.startTracking()
                    }
                }
            }
        )

        Box { compass() }

        Box { LocationInfoOverlay() }
    }
}

@Composable
fun HeaderButton(isSearching: Boolean) {
    val appViewModel: AppViewModel = getViewModel()
    val focusManager = LocalFocusManager.current

    if (isSearching) {
        RoundedIconButton(
            modifier = Modifier.testTag("SearchBackButton"),
            bordered = true,
            onClick = { focusManager.clearFocus() },
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
        )
    } else {
        RoundedIconButton(
            modifier = Modifier.testTag("MenuButton"),
            bordered = true,
            onClick = { appViewModel.setMenuOpen(true) },
            imageVector = Icons.Rounded.Menu,
            contentDescription = "Menu",
        )
    }
}
