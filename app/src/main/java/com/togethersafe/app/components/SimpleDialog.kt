package com.togethersafe.app.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel

@Composable
fun SimpleDialog() {
    val appViewModel: AppViewModel = getViewModel()
    val dialogState by appViewModel.dialogState.collectAsState()
    val closeDialog: () -> Unit = { appViewModel.setDialogState(null) }

    if (dialogState != null) {
        val state = dialogState!!

        AlertDialog(
            onDismissRequest = {
                state.onDismiss?.invoke()
                closeDialog()
            },
            title = { Text(text = state.title) },
            text = { Text(text = state.message) },
            confirmButton = {
                TextButton(onClick = {
                    state.onConfirm()
                    closeDialog()
                }) {
                    Text(state.confirmText)
                }
            },
            dismissButton = state.onDismiss?.let {
                {
                    TextButton(onClick = {
                        it()
                        closeDialog()
                    }) {
                        Text(state.dismissText)
                    }
                }
            }
        )
    }
}
