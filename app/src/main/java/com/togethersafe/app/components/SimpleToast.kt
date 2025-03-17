package com.togethersafe.app.components

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.togethersafe.app.viewmodels.AppViewModel

@Composable
fun SimpleToast() {
    val activity = LocalActivity.current as ComponentActivity
    val appViewModel: AppViewModel = hiltViewModel(activity)
    val message by appViewModel.toastMessage.collectAsState()

    if (message.isNotBlank()) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        appViewModel.setToastMessage("")
    }
}
