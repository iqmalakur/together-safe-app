package com.togethersafe.app.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.togethersafe.app.data.model.Incident

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(incident: Incident, handleDismissRequest: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = handleDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 30.dp)
        ) {
            BottomSheetHeader(incident)
            BottomSheetContent(incident)
            BottomSheetMedia(incident)
            BottomSheetReport(incident)
        }
    }
}

@Composable
private fun BottomSheetHeader(incident: Incident) {
    Text(
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
//    InfoText("Jumlah Laporan", incident.reportCount.toString())

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun InfoText(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun BottomSheetMedia(incident: Incident) {
    SectionTitle("Bukti Gambar/Video")

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//        items(incident.mediaUrls.take(3)) { mediaUrl ->
//            AsyncImage(
//                model = mediaUrl,
//                contentDescription = "Bukti Insiden",
//                modifier = Modifier
//                    .size(120.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
//                contentScale = ContentScale.Crop
//            )
//        }
    }

    SeeMore { /* TODO: Navigasi ke galeri insiden */ }
}

@Composable
private fun BottomSheetReport(incident: Incident) {
    SectionTitle("Laporan Terkait")

    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
//        items(incident.reports.take(3)) { report ->
//            Text(text = "- $report", style = MaterialTheme.typography.bodySmall)
//        }
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
