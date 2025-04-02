package com.togethersafe.app.views.report

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.togethersafe.app.components.InputTextField
import com.togethersafe.app.components.OutlinedRoundedButton
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.MapViewModel

@Composable
fun ReportForm(enableScroll: () -> Unit, disableScroll: () -> Unit) {
    val mapViewModel: MapViewModel = getViewModel()

    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(mapViewModel.cameraPosition.value) }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    Column {
        InputTextField(
            label = "Deskripsi",
            value = description,
            onValueChange = { description = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LocationPicker(
            value = location,
            enableScroll = enableScroll,
            disableScroll = disableScroll,
        ) { location = it }
        Spacer(modifier = Modifier.height(16.dp))

        DatePickerField(date) { date = it }
        Spacer(modifier = Modifier.height(16.dp))

        TimePickerField(time) { time = it }
        Spacer(modifier = Modifier.height(16.dp))

        ImagePicker(
            imageUris = imageUris,
            onAddImage = { imageUris += it },
            onDeleteImage = { imageUris -= it },
        )

        OutlinedRoundedButton(
            text = "Kirim Laporan",
            onClick = { /* TODO: Handle submit action */ },
            testTag = "",
            contentDescription = "",
        )
    }
}
