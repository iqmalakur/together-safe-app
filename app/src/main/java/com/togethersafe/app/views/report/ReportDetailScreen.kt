package com.togethersafe.app.views.report

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.togethersafe.app.components.AppHeader
import com.togethersafe.app.navigation.LocalNavController
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.ReportViewModel

@Composable
fun ReportDetailScreen() {
    val navController = LocalNavController.current
    val focusManager = LocalFocusManager.current

    val appViewModel: AppViewModel = getViewModel()
    val reportViewModel: ReportViewModel = getViewModel()
    val report by reportViewModel.report.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AppHeader("Detail Laporan Insiden") {
                appViewModel.setLoadIncident(true)
                navController.popBackStack()
                reportViewModel.resetReport()
            }

            report?.let {  ReportDetailContent(it) } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada data laporan.")
                }
            }
        }
    }
}
