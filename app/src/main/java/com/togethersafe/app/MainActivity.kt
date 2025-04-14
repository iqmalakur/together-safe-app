package com.togethersafe.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.togethersafe.app.components.LocationPermissionHandler
import com.togethersafe.app.components.SimpleDialog
import com.togethersafe.app.components.SimpleToast
import com.togethersafe.app.navigation.AppNavigation
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

        setContent {
            LaunchedEffect(Unit) {
                if (isPermissionGranted(this@MainActivity))
                    getCurrentLocation(this@MainActivity) {}

                val cameraPosition = mapViewModel.cameraPosition.value
                incidentViewModel.loadIncidents(cameraPosition) { _, errors ->
                    appViewModel.setToastMessage(errors[0])
                }

                authViewModel.validateToken()
            }

            AppNavigation()

            LocationPermissionHandler()
            SimpleToast()
            SimpleDialog()
        }
    }
}
