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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.togethersafe.app.components.ErrorMessages
import com.togethersafe.app.components.InputTextField
import com.togethersafe.app.components.OutlinedRoundedButton
import com.togethersafe.app.data.dto.CategoryResDto
import com.togethersafe.app.data.dto.ReportReqDto
import com.togethersafe.app.navigation.LocalNavController
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.utils.uriToFile
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.MapViewModel
import com.togethersafe.app.viewmodels.ReportViewModel

@Composable
fun ReportForm(enableScroll: () -> Unit, disableScroll: () -> Unit) {
    val appViewModel: AppViewModel = getViewModel()
    val mapViewModel: MapViewModel = getViewModel()
    val reportViewModel: ReportViewModel = getViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current

    var errorMessages by remember { mutableStateOf<List<String>>(emptyList()) }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(mapViewModel.cameraPosition.value) }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<CategoryResDto?>(null) }

    Column {
        CategoryDropdown(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(16.dp))
        ErrorMessages(errorMessages)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedRoundedButton(
            text = "Kirim Laporan",
            onClick = {
                appViewModel.setLoading(true)

                val media = imageUris.mapNotNull { uriToFile(context, it) }
                reportViewModel.createReport(
                    ReportReqDto(
                        description = description,
                        location = location,
                        date = date,
                        time = time,
                        media = media,
                        categoryId = selectedCategory?.id ?: -1,
                    ),
                    onError = { _, messages -> errorMessages = messages },
                    onComplete = { appViewModel.setLoading(false) },
                    onSuccess = {
                        appViewModel.setLoadIncident(true)
                        navController.popBackStack()
                    },
                )
            },
            testTag = "",
            contentDescription = "",
        )
    }
}
