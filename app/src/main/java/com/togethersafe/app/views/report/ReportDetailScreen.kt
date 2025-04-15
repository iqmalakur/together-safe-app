package com.togethersafe.app.views.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.togethersafe.app.components.AppHeader
import com.togethersafe.app.components.UserProfile
import com.togethersafe.app.navigation.LocalNavController
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.ReportViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReportDetailScreen() {
    val navController = LocalNavController.current
    val reportViewModel: ReportViewModel = getViewModel()
    val report by reportViewModel.report.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppHeader("Detail Laporan Insiden") {
            navController.popBackStack()
            reportViewModel.resetReport()
        }

        report?.let { report ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = report.incident.category,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${report.date} • ${report.time}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserProfile(
                            imageModel = report.user.profilePhoto,
                            size = 48.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = report.user.name, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Reputasi: ${report.user.reputation}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Deskripsi:",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = report.description)
                }

                item {
                    Text(
                        text = "Lokasi:",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = report.location)
                }

                if (report.attachments.isNotEmpty()) {
                    item {
                        Text(
                            text = "Lampiran:",
                            fontWeight = FontWeight.SemiBold
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(report.attachments) { attachmentUrl ->
                                AsyncImage(
                                    model = attachmentUrl,
                                    contentDescription = "Lampiran",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("👍 ${report.upvote}")
                        Text("👎 ${report.downvote}")
                        Text("Status: ${report.status}")
                    }
                }

                item {
                    Text(
                        text = "Komentar:",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(report.comments) { comment ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserProfile(
                                imageModel = comment.user.profilePhoto,
                                size = 36.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = comment.user.name,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Reputasi: ${comment.user.reputation}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = comment.comment)
                        Text(
                            text = "${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(comment.createdAt)}${if (comment.isEdited) " (diedit)" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Tidak ada data laporan.")
            }
        }
    }
}
