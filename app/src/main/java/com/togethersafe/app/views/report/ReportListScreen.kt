package com.togethersafe.app.views.report

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
import com.togethersafe.app.navigation.LocalNavController
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.ReportViewModel

@Composable
fun ReportListScreen() {
    val appViewModel: AppViewModel = getViewModel()
    val reportViewModel: ReportViewModel = getViewModel()

    val navController = LocalNavController.current
    val reports by reportViewModel.reportList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppHeader("Laporan Insiden") {
            navController.popBackStack()
            reportViewModel.setReportList(emptyList())
            appViewModel.setLoadIncident(true)
        }

        if (reports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada laporan.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reports) { report ->
                    ItemCard(
                        title = report.category,
                        description = report.description,
                        location = report.location,
                        date = report.date,
                        time = report.time,
                        onClick = {
                            appViewModel.setLoading(true)
                            reportViewModel.fetchDetailReport(
                                id = report.id,
                                onError = { _, messages -> appViewModel.setToastMessage(messages[0]) },
                                onComplete = { appViewModel.setLoading(false) }
                            ) {
                                navController.navigate("report-detail")
                            }
                        }
                    )
                }
            }
        }
    }
}

