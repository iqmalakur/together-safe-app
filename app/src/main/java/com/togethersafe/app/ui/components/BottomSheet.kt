package com.togethersafe.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(content: String, sheetState: SheetState, handleDismissRequest: () -> Unit) {
    val incidentCategory = "Pembegalan"
    val incidentDescription = "Terjadi pembegalan di daerah Jalan Raya Cimahi pada malam hari. Pelaku menggunakan motor dan membawa senjata tajam."
    val incidentLocation = "Jalan Raya Cimahi"
    val incidentTime = "17 Februari 2025, 22:30 WIB"
    val incidentStatus = "Terverifikasi"
    val reportCount = 5

    val reports = listOf(
        "Saya melihat kejadian ini sekitar pukul 22:15 WIB.",
        "Pelaku membawa senjata tajam dan merampas motor korban.",
        "Korban mengalami luka ringan dan sudah melapor ke polisi.",
        "Pelaku kabur ke arah jalan tol setelah kejadian.",
        "Beberapa warga mencoba mengejar pelaku tetapi gagal."
    )

    val mediaUrls = listOf(
        "https://source.unsplash.com/300x200/?crime,street",
        "https://source.unsplash.com/300x200/?accident,night",
        "https://source.unsplash.com/300x200/?police,incident"
    )

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = handleDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 30.dp)
        ) {
            Text(
                text = incidentCategory,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = incidentDescription,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📍 Lokasi: $incidentLocation",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "🕒 Waktu: $incidentTime",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "✅ Status: $incidentStatus",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Green
            )

            Text(
                text = "📊 Jumlah Laporan: $reportCount",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bukti Gambar/Video",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyRow {
                items(mediaUrls.take(3)) { mediaUrl ->
                    AsyncImage(
                        model = mediaUrl,
                        contentDescription = "Bukti Insiden",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Text(
                text = "Lihat Selengkapnya...",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { /* TODO: Navigasi ke galeri bukti */ }
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Laporan Terkait",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn {
                items(reports.take(3)) { report ->
                    Text(text = "- $report", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Text(
                text = "Lihat Selengkapnya...",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { /* TODO: Navigasi ke halaman laporan lengkap */ }
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
