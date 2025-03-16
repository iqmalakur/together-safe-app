package com.togethersafe.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.togethersafe.app.components.DoubleBackHandler
import com.togethersafe.app.components.LocationPermissionHandler
import com.togethersafe.app.components.SimpleToast
import com.togethersafe.app.utils.getCurrentLocation
import com.togethersafe.app.utils.isPermissionGranted
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.views.map.MapScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()
    private val incidentViewModel: IncidentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isPermissionGranted(this)) getCurrentLocation(this) {}

        incidentViewModel.loadIncidents()

        setContent {
            val errorIncident by incidentViewModel.error.collectAsState()

            LaunchedEffect(errorIncident) {
                if (errorIncident != null) appViewModel.setToastMessage(errorIncident!!)
            }

            MapScreen()

            LocationPermissionHandler()
            SimpleToast()
            DoubleBackHandler()
        }
    }
}
