package com.togethersafe.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.togethersafe.app.components.LoadingOverlay
import com.togethersafe.app.components.LocationPermissionHandler
import com.togethersafe.app.components.SimpleDialog
import com.togethersafe.app.components.SimpleToast
import com.togethersafe.app.navigation.AppNavigation
import com.togethersafe.app.utils.NetworkConnectivity
import com.togethersafe.app.utils.NetworkMonitor
import com.togethersafe.app.utils.getCurrentLocation
import com.togethersafe.app.utils.isPermissionGranted
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.AuthViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.viewmodels.MapViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()
    private val incidentViewModel: IncidentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val networkMonitor = NetworkMonitor(applicationContext)

        setContent {
            val isLoadIncident by appViewModel.isLoadIncident.collectAsState()

            LaunchedEffect(Unit) {
                if (isPermissionGranted(this@MainActivity))
                    getCurrentLocation(this@MainActivity) {}

                authViewModel.validateToken()
            }

            LaunchedEffect(isLoadIncident) {
                if (isLoadIncident) {
                    val cameraPosition = mapViewModel.cameraPosition.value
                    incidentViewModel.loadIncidents(cameraPosition) { _, errors ->
                        appViewModel.setToastMessage(errors[0])
                    }

                    appViewModel.setLoadIncident(false)
                }
            }

            LoadingOverlay()

            AppNavigation()

            NetworkConnectivity(networkMonitor)
            LocationPermissionHandler()
            SimpleToast()
            SimpleDialog()
        }
    }
}
