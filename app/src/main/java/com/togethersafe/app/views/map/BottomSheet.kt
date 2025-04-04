package com.togethersafe.app.views.map

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.togethersafe.app.data.model.Incident
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(sheetState: SheetState) {
    val incidentViewModel: IncidentViewModel = getViewModel()
    val selectedIncident by incidentViewModel.selectedIncident.collectAsState()

    ModalBottomSheet(
        modifier = Modifier.testTag("BottomSheet"),
        sheetState = sheetState,
        onDismissRequest = { incidentViewModel.setSelectedIncident(null) },
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
private fun BottomSheetHeader(incident: Incident) {
    Text(
        modifier = Modifier.testTag("IncidentDetail-Kategori"),
        text = incident.category,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(8.dp))

//    Text(
//        text = incident.description,
//        style = MaterialTheme.typography.bodyMedium
//    )
//
//    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun BottomSheetContent(incident: Incident) {
    InfoText("Lokasi", incident.location)
    InfoText("Tanggal", incident.date)
    InfoText("Jam", incident.time)
    InfoText("Tingkat Risiko", incident.riskLevel)
    InfoText("Status", incident.status)
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
private fun BottomSheetMedia(incident: Incident) {
    SectionTitle("Bukti Gambar/Video")

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(incident.mediaUrls.take(3)) { index, mediaUrl ->
            AsyncImage(
                model = mediaUrl,
                contentDescription = "Bukti Insiden",
                modifier = Modifier
                    .testTag("IncidentDetail-Image-$index")
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }

    SeeMore { /* TODO: Navigasi ke galeri insiden */ }
}

@Composable
private fun BottomSheetReport(incident: Incident) {
    SectionTitle("Laporan Terkait")

    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        itemsIndexed(incident.reports.take(3)) { index, report ->
            Text(
                modifier = Modifier.testTag("IncidentDetail-Report-$index"),
                text = "- ${report.description}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

    SeeMore { /* TODO: Navigasi ke list laporan insiden */ }
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
private fun SeeMore(onClick: () -> Unit) {
    Text(
        text = "Lihat Selengkapnya...",
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))
}
