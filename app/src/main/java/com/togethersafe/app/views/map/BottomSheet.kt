package com.togethersafe.app.views.map

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.togethersafe.app.data.dto.IncidentDetailResDto
import com.togethersafe.app.navigation.LocalNavController
import com.togethersafe.app.utils.getFormattedIncidentRisk
import com.togethersafe.app.utils.getFormattedIncidentStatus
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel
import com.togethersafe.app.viewmodels.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(sheetState: SheetState) {
    val incidentViewModel: IncidentViewModel = getViewModel()
    val selectedIncident by incidentViewModel.selectedIncident.collectAsState()

    ModalBottomSheet(
        modifier = Modifier.testTag("BottomSheet"),
        sheetState = sheetState,
        onDismissRequest = { incidentViewModel.clearSelectedIncident() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 30.dp)
        ) {
            BottomSheetHeader(selectedIncident!!)
            BottomSheetContent(selectedIncident!!)
            BottomSheetMedia(selectedIncident!!)
            BottomSheetReport(selectedIncident!!)
        }
    }
}

@Composable
private fun BottomSheetHeader(incident: IncidentDetailResDto) {
    Text(
        modifier = Modifier.testTag("IncidentDetail-Kategori"),
        text = incident.category,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun BottomSheetContent(incident: IncidentDetailResDto) {
    InfoText("Lokasi", incident.location)
    InfoText("Tanggal", incident.date)
    InfoText("Jam", incident.time)
    InfoText("Tingkat Risiko", getFormattedIncidentRisk(incident.riskLevel))
    InfoText("Status", getFormattedIncidentStatus(incident.status))
    InfoText("Total Upvote", "${incident.upvoteCount}")
    InfoText("Total Downvote", "${incident.downvoteCount}")
    InfoText("Jumlah Laporan", "${incident.reports.size}")

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun InfoText(label: String, value: String) {
    Text(
        modifier = Modifier.testTag("IncidentDetail-$label"),
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun BottomSheetMedia(incident: IncidentDetailResDto) {
    SectionTitle("Lampiran Gambar")

    if (incident.mediaUrls.isNotEmpty()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(incident.mediaUrls.take(3)) { index, mediaUrl ->
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = "Lampiran Gambar",
                    modifier = Modifier
                        .testTag("IncidentDetail-Image-$index")
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    } else {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Tidak ada lampiran gambar",
            textAlign = TextAlign.Center,
            color = Color.Gray,
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun BottomSheetReport(
    incident: IncidentDetailResDto
) {
    val appViewModel: AppViewModel = getViewModel()
    val reportViewModel: ReportViewModel = getViewModel()

    val navController = LocalNavController.current

    SectionTitle("Laporan Terkait")

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(incident.reports.take(3)) { index, report ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        appViewModel.setLoading(true)
                        reportViewModel.fetchDetailReport(
                            id = report.id,
                            onError = { _, messages -> appViewModel.setToastMessage(messages[0]) },
                            onComplete = { appViewModel.setLoading(false) }
                        ) {
                            navController.navigate("report-detail")
                        }
                    }
                    .testTag("IncidentDetail-Report-$index")
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Report,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = report.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    SeeMore(incident.id)
}


@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SeeMore(incidentId: String) {
    val appViewModel: AppViewModel = getViewModel()
    val incidentViewModel: IncidentViewModel = getViewModel()
    val reportViewModel: ReportViewModel = getViewModel()
    val navController = LocalNavController.current

    Text(
        text = "Lihat Selengkapnya...",
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clickable {
                appViewModel.setLoading(true)
                incidentViewModel.fetchIncidentReports(
                    incidentId = incidentId,
                    onError = { _, messages -> appViewModel.setToastMessage(messages[0]) },
                    onComplete = { appViewModel.setLoading(false) }
                ) { reports ->
                    reportViewModel.setReportList(reports)
                    incidentViewModel.clearSelectedIncident()
                    navController.navigate("report-list")
                }
            }
            .padding(vertical = 8.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))
}
