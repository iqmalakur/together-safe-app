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
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.togethersafe.app.components.RoundedIconButton
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun ActionButton(
    mapViewModel: MapViewModel = hiltViewModel(),
    compass: @Composable () -> Unit,
) {
    val isTracking by mapViewModel.isTracking.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
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
            onClick = { mapViewModel.startTracking() }
        )

        Box { compass() }
    }
}

@Composable
fun HeaderButton(
    isSearching: Boolean,
    appViewModel: AppViewModel = hiltViewModel(),
) {
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
