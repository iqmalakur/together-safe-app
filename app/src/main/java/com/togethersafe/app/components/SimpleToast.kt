package com.togethersafe.app.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.togethersafe.app.viewmodels.AppViewModel

@Composable
fun SimpleToast(appViewModel: AppViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val message by appViewModel.toastMessage.collectAsState()

    if (message.isNotBlank()) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        appViewModel.setToastMessage("")
    }
}
