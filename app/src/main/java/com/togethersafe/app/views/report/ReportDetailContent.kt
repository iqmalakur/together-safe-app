package com.togethersafe.app.views.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.togethersafe.app.components.UserProfile
import com.togethersafe.app.data.dto.CommentResDto
import com.togethersafe.app.data.dto.ReportResDto
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.AuthViewModel
import com.togethersafe.app.viewmodels.ReportInteractionViewModel

@Composable
fun ColumnScope.ReportDetailContent(report: ReportResDto) {
    val listState = rememberLazyListState()

    val appViewModel: AppViewModel = getViewModel()
    val authViewModel: AuthViewModel = getViewModel()
    val reportInteractionViewModel: ReportInteractionViewModel = getViewModel()

    var isLoggedIn by remember { mutableStateOf(false) }
    var isUserOwnReport by remember { mutableStateOf(false) }
    var userEmail by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf<List<CommentResDto>>(emptyList()) }

    LaunchedEffect(report) {
        comments = report.comments

        authViewModel.user.value?.let { user ->
            isLoggedIn = true
            isUserOwnReport = report.user.email == user.email
            userEmail = user.email
        }
    }

    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = listState,
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
                VoteButtons(report, isLoggedIn, isUserOwnReport)
                Text("Status: ${report.status}")
            }
        }

        item {
            Text(
                text = "Komentar:",
                fontWeight = FontWeight.SemiBold
            )
        }

        items(comments) { comment ->
            CommentItem(
                comment = comment,
                loggedInUserEmail = userEmail,
                appViewModel = appViewModel,
                onDelete = {
                    appViewModel.setLoading(true)
                    reportInteractionViewModel.deleteComment(
                        id = comment.id,
                        onError = { _, messages -> appViewModel.setToastMessage(messages[0]) },
                        onSuccess = { id ->
                            comments = comments.filter { it.id != id }
                            appViewModel.setToastMessage("Komentar berhasil dihapus")
                        }
                    ) { appViewModel.setLoading(false) }
                },
                onEdit = { newComment ->
                    appViewModel.setLoading(true)
                    reportInteractionViewModel.editComment(
                        id = comment.id,
                        comment = newComment,
                        onError = { _, messages -> appViewModel.setToastMessage(messages[0]) },
                        onSuccess = { comment ->
                            comments = comments.map {
                                when (it.id) {
                                    comment.id -> comment
                                    else -> it
                                }
                            }
                            appViewModel.setToastMessage("Komentar berhasil diedit")
                        }
                    ) { appViewModel.setLoading(false) }
                }
            )
        }
    }

    if (isLoggedIn) {
        CommentTextField(report.id) {
            comments += it
            listState.scrollToItem(listState.layoutInfo.totalItemsCount - 1)
        }
    }
}
