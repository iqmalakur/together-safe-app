package com.togethersafe.app.views.incident

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.togethersafe.app.components.AppHeader
import com.togethersafe.app.components.ItemCard
import com.togethersafe.app.utils.getFormattedIncidentRisk
import com.togethersafe.app.utils.getFormattedIncidentStatus
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun IncidentListScreen() {
    val incidentViewModel: IncidentViewModel = getViewModel()
    val appViewModel: AppViewModel = getViewModel()
    val mapViewModel: MapViewModel = getViewModel()

    val incidents by incidentViewModel.incidents.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppHeader("Insiden Sekitar") { appViewModel.setShowIncidentList(false) }

        if (incidents.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada insiden di sekitar.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(incidents) { incident ->
                    ItemCard(
                        title = incident.category,
                        description = "Potensi risiko: ${getFormattedIncidentRisk(incident.riskLevel)}",
                        location = incident.location,
                        date = incident.date,
                        time = incident.time,
                        status = getFormattedIncidentStatus(incident.status),
                        onClick = {
                            appViewModel.setLoading(true)
                            incidentViewModel.fetchIncidentById(
                                id = incident.id,
                                onError = { _, messages -> appViewModel.setToastMessage(messages[0]) },
                            ) {
                                appViewModel.setLoading(false)
                                appViewModel.setMenuOpen(false)
                                appViewModel.setShowIncidentList(false)
                                mapViewModel.setCameraPosition(
                                    incident.latitude,
                                    incident.longitude
                                )
                                mapViewModel.setZoomLevel(19.0)
                            }
                        }
                    )
                }
            }
        }
    }

    BackHandler { appViewModel.setShowIncidentList(false) }
}

