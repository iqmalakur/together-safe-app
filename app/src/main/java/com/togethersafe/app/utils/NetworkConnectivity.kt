package com.togethersafe.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.AuthViewModel

@Composable
fun NetworkConnectivity(networkMonitor: NetworkMonitor) {
    val appViewModel: AppViewModel = getViewModel()
    val authViewModel: AuthViewModel = getViewModel()
    val isConnected by networkMonitor.isConnected.collectAsState(initial = false)

    var wasConnected by remember { mutableStateOf(false) }

    LaunchedEffect(isConnected) {
        if (isConnected && !wasConnected) {
            appViewModel.setLoadIncident(true)
            authViewModel.validateToken()
        }
        wasConnected = isConnected
    }
}

