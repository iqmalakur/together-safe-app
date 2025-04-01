package com.togethersafe.app.components

import androidx.compose.runtime.Composable
import com.togethersafe.app.data.model.DialogState
import com.togethersafe.app.navigation.LocalNavController
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel

@Composable
fun LoginRequired(content: @Composable (showDialog: () -> Unit) -> Unit) {
    val appViewModel: AppViewModel = getViewModel()
    val navController = LocalNavController.current

    content {
        appViewModel.setDialogState(
            DialogState(
                title = "Login Diperlukan",
                message = "Anda harus login terlebih dahulu untuk mengakses fitur ini.",
                confirmText = "Login",
                onConfirm = {
                    navController.navigate("login")
                    appViewModel.setMenuOpen(false)
                },
                onDismiss = {},
            )
        )
    }
}
