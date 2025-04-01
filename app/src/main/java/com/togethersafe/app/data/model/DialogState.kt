package com.togethersafe.app.data.model

data class DialogState(
    val title: String,
    val message: String,
    val onConfirm: () -> Unit,
    val onDismiss: (() -> Unit)? = null,
    val confirmText: String = "OK",
    val dismissText: String = "Batal",
)
