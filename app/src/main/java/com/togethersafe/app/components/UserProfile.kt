package com.togethersafe.app.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun UserProfile(
    imageModel: Any?,
    size: Dp = 48.dp,
) {
    if (imageModel != null) {
        AsyncImage(
            model = imageModel,
            contentDescription = "Foto Profil",
            modifier = Modifier
                .testTag("UserProfile")
                .size(size)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Foto Profil",
            modifier = Modifier
                .testTag("UserProfileDefault")
                .size(size)
        )
    }
}
