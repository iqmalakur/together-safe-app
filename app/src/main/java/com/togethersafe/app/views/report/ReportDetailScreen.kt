package com.togethersafe.app.views.report

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.togethersafe.app.data.dto.ReportResDto
import com.togethersafe.app.navigation.LocalNavController
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.AuthViewModel
import com.togethersafe.app.viewmodels.ReportInteractionViewModel
import com.togethersafe.app.viewmodels.ReportViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReportDetailScreen() {
    val navController = LocalNavController.current

    val appViewModel: AppViewModel = getViewModel()
    val authViewModel: AuthViewModel = getViewModel()
    val reportViewModel: ReportViewModel = getViewModel()
    val reportInteractionViewModel: ReportInteractionViewModel = getViewModel()

    val report by reportViewModel.report.collectAsState()

    var isUserOwnReport by remember { mutableStateOf(false) }
    var upvoteCount by remember { mutableIntStateOf(0) }
    var downvoteCount by remember { mutableIntStateOf(0) }
    var currentVote by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(report) {
        if (report != null) {
            upvoteCount = report!!.upvote
            downvoteCount = report!!.downvote

            authViewModel.user.value?.let {
                isUserOwnReport = report!!.user.email == it.email
            }

            reportInteractionViewModel.findUserVote(
                report!!.id,
                onError = { _, messages -> appViewModel.setToastMessage(messages[0]) },
            ) { currentVote = it }
        }
    }

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
                reportDetailContent(report) {
                    VoteButton(
                        type = "upvote",
                        text = "👍 $upvoteCount",
                        reportId = report.id,
                        currentVote = currentVote,
                        enabled = !isUserOwnReport,
                    ) {
                        when (currentVote) {
                            null -> upvoteCount++
                            "upvote" -> upvoteCount--
                            else -> {
                                upvoteCount++
                                downvoteCount--
                            }
                        }

                        currentVote = it
                    }

                    VoteButton(
                        type = "downvote",
                        text = "👎 $downvoteCount",
                        reportId = report.id,
                        currentVote = currentVote,
                        enabled = !isUserOwnReport,
                    ) {
                        when (currentVote) {
                            null -> downvoteCount++
                            "downvote" -> downvoteCount--
                            else -> {
                                downvoteCount++
                                upvoteCount--
                            }
                        }

                        currentVote = it
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

private fun LazyListScope.reportDetailContent(
    report: ReportResDto,
    voteButtons: @Composable () -> Unit
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
            Spacer(Modifier.height(4.dp))
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
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            voteButtons()
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
                text = "${
                    SimpleDateFormat(
                        "dd MMM yyyy, HH:mm",
                        Locale.getDefault()
                    ).format(comment.createdAt)
                }${if (comment.isEdited) " (diedit)" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
private fun VoteButton(
    type: String,
    text: String,
    reportId: String,
    currentVote: String?,
    enabled: Boolean,
    onSuccessVote: (newVote: String?) -> Unit
) {
    val appViewModel: AppViewModel = getViewModel()
    val reportInteractionViewModel: ReportInteractionViewModel = getViewModel()

    TextButton(
        colors = ButtonDefaults.textButtonColors(
            containerColor = when (currentVote) {
                type -> Color.Gray
                else -> Color.White
            },
            contentColor = when (currentVote) {
                type -> Color.White
                else -> Color.Black
            },
        ),
        border = BorderStroke(1.dp, Color.Black.copy(0.2f)),
        enabled = enabled,
        onClick = {
            appViewModel.setLoading(true)
            reportInteractionViewModel.vote(
                reportId = reportId,
                prevVoteType = currentVote,
                targetVoteType = type,
                onError = { _, messages -> appViewModel.setToastMessage(messages[0]) },
                onSuccess = onSuccessVote
            ) { appViewModel.setLoading(false) }
        }
    ) {
        Text(text)
    }
}
