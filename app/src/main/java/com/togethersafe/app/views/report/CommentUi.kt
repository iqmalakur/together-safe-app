package com.togethersafe.app.views.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.togethersafe.app.components.UserProfile
import com.togethersafe.app.data.dto.CommentResDto
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.ReportInteractionViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CommentTextField(reportId: String, onSuccessSend: suspend (CommentResDto) -> Unit) {
    val appViewModel: AppViewModel = getViewModel()
    val reportInteractionViewModel: ReportInteractionViewModel = getViewModel()
    var commentText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Tulis komentar...") },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
            ),
            maxLines = 5,
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            appViewModel.setLoading(true)
                            reportInteractionViewModel.createComment(
                                reportId = reportId,
                                comment = commentText,
                                onError = { _, messages ->
                                    appViewModel.setToastMessage(messages[0])
                                },
                                onSuccess = {
                                    commentText = ""
                                    onSuccessSend(it)
                                }
                            ) { appViewModel.setLoading(false) }
                        }
                    },
                    enabled = commentText.isNotBlank()
                ) {
                    val tint =
                        if (commentText.isNotBlank()) MaterialTheme.colorScheme.primary
                        else Color.Gray

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim komentar",
                        tint = tint
                    )
                }
            }
        )
    }
}

@Composable
fun CommentItem(comment: CommentResDto) {
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
