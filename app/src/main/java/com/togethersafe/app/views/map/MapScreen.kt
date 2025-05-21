package com.togethersafe.app.views.map

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.togethersafe.app.components.DoubleBackHandler
import com.togethersafe.app.components.IncidentWarningCard
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.views.incident.IncidentListScreen

@Composable
fun MapScreen() {
    val appViewModel: AppViewModel = getViewModel()
    val isShowIncidentList by appViewModel.isShowIncidentList.collectAsState()

    NavigationDrawer {
        Box {
            Map()
            MapHeader()
            IncidentWarningCard()
        }
    }

    if (isShowIncidentList) IncidentListScreen()
    else DoubleBackHandler()
}
