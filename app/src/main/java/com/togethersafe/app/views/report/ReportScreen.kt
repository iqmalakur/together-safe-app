package com.togethersafe.app.views.report

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.togethersafe.app.components.AppHeader
import com.togethersafe.app.navigation.LocalNavController

@Composable
fun ReportScreen() {
    val navController = LocalNavController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) {
        AppHeader(
            text = "Tambah Laporan",
            onClick = { navController.popBackStack() }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            ReportForm()
        }
    }
}