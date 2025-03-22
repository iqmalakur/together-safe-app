package com.togethersafe.app.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.togethersafe.app.utils.getActivity
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel

@Composable
fun SimpleToast() {
    val activity = getActivity()
    val appViewModel: AppViewModel = getViewModel()
    val message by appViewModel.toastMessage.collectAsState()

    if (message.isNotBlank()) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        appViewModel.setToastMessage("")
    }
}
