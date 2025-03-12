package com.togethersafe.app.ui.components

import androidx.activity.compose.BackHandler
import android.os.Process
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.togethersafe.app.ui.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DoubleBackHandler(appViewModel: AppViewModel = hiltViewModel()) {
    var backPressedOnce by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        if (backPressedOnce) {
            Process.killProcess(Process.myPid())
        } else {
            backPressedOnce = true
            appViewModel.setToastMessage("Tekan sekali lagi untuk keluar")

            coroutineScope.launch {
                delay(2000)
                backPressedOnce = false
            }
        }
    }
}
