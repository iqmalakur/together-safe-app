package com.togethersafe.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.runtime.Composable

@Composable
fun ZoomButton(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
) {
    RoundedIconButton(
        imageVector = Icons.Rounded.ZoomIn,
        contentDescription = "Perbesar Peta",
        onClick = onZoomIn
    )

    RoundedIconButton(
        imageVector = Icons.Rounded.ZoomOut,
        contentDescription = "Perkecil Peta",
        onClick = onZoomOut
    )
}
