package com.togethersafe.app.components

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun GalleryLauncher(
    onResult: (uri: Uri) -> Unit,
    content: @Composable (launcher: ManagedActivityResultLauncher<String, Uri?>) -> Unit
) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onResult(it) }
        }

    content(launcher)
}

@Composable
fun CameraLauncher(
    onResult: (uri: Uri) -> Unit,
    content: @Composable (startLauncher: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val photoUri = remember { mutableStateOf<Uri?>(null) }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUri.value != null) {
                onResult(photoUri.value!!)
            }
        }

    val createImageFile: () -> Uri = {
        val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        photoUri.value = uri
        uri
    }

    val startLauncher: () -> Unit = {
        val uri = createImageFile()
        launcher.launch(uri)
    }

    content(startLauncher)
}
