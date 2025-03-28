package com.togethersafe.app.views.register

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.togethersafe.app.components.CameraLauncher
import com.togethersafe.app.components.GalleryLauncher
import com.togethersafe.app.components.UserProfile

@Composable
fun ProfileImagePicker(
    imageUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        UserProfile(imageUri, 100.dp)

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            GalleryLauncher(
                onResult = { uri -> onImageSelected(uri) }
            ) { launcher ->
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Dari Galeri")
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            CameraLauncher(
                onResult = { uri -> onImageSelected(uri) }
            ) { startLauncher ->
                Button(onClick = { startLauncher() }) {
                    Text("Ambil Foto")
                }
            }
        }
    }
}
