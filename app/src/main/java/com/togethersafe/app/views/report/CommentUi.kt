package com.togethersafe.app.views.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.togethersafe.app.data.model.DialogState
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
fun CommentItem(
    comment: CommentResDto,
    loggedInUserEmail: String,
    appViewModel: AppViewModel,
    onDelete: () -> Unit,
    onEdit: (newComment: String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedText by remember { mutableStateOf(comment.comment) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            UserProfile(
                imageModel = comment.user.profilePhoto,
                size = 36.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = comment.user.name,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${comment.user.email}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (comment.user.email == loggedInUserEmail) {
                Row {
                    IconButton(
                        onClick = { isEditing = !isEditing },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = "Edit komentar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            appViewModel.setDialogState(
                                DialogState(
                                    title = "Hapus Komentar",
                                    message = "Apakah Anda yakin ingin menghapus komentar ini?",
                                    confirmText = "Hapus",
                                    dismissText = "Batal",
                                    onConfirm = onDelete,
                                    onDismiss = {}
                                )
                            )
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus komentar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        if (isEditing) {
            OutlinedTextField(
                value = editedText,
                onValueChange = { editedText = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onEdit(editedText)
                            isEditing = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Simpan perubahan",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        } else {
            Text(text = comment.comment)
        }

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
