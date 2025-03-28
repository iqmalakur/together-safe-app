package com.togethersafe.app.views.report

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.togethersafe.app.components.CameraLauncher
import com.togethersafe.app.components.GalleryLauncher

@Composable
fun ImagePicker(
    imageUris: List<Uri>,
    onAddImage: (Uri) -> Unit,
    onDeleteImage: (Uri) -> Unit,
) {
    val listState = rememberLazyListState()
    var shouldScroll by remember { mutableStateOf(false) }

    LaunchedEffect(shouldScroll) {
        if (shouldScroll) {
            listState.animateScrollToItem(imageUris.size)
            shouldScroll = false
        }
    }

    Text(text = "Lampiran Gambar (Maksimal 5)", style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(4.dp))

    CameraLauncher(
        onResult = { uri ->
            if (imageUris.size < 5) {
                onAddImage(uri)
                shouldScroll = imageUris.size >= 2
            }
        }
    ) { startLauncher ->
        Button(
            enabled = imageUris.size < 5,
            onClick = { startLauncher() },
            colors = ButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.LightGray
            )
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Ambil gambar",
            )
            Spacer(Modifier.width(8.dp))
            Text("Ambil gambar")
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(imageUris) { uri -> ImageItem(uri, onDeleteImage) }
        if (imageUris.size < 5) {
            item {
                GalleryLauncher(
                    onResult = { uri ->
                        if (imageUris.size < 5) {
                            onAddImage(uri)
                            shouldScroll = imageUris.size >= 2
                        }
                    }
                ) { launcher -> AddImage(launcher) }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun ImageItem(uri: Uri, onDeleteImage: (Uri) -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = "Lampiran Gambar",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = { onDeleteImage(uri) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Hapus Gambar",
                tint = Color.Red,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun AddImage(galleryLauncher: ManagedActivityResultLauncher<String, Uri?>) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { galleryLauncher.launch("image/*") },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Tambah dari Galeri",
            tint = Color.Gray
        )
    }
}

