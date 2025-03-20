package com.togethersafe.app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun RoundedIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String,
    bordered: Boolean = false,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier
            .padding(5.dp)
            .clip(CircleShape)
            .background(Color.White)
            .then(
                if (bordered) Modifier.border(1.dp, Color.Gray, CircleShape) else Modifier
            ),
        onClick = onClick
    ) {
        Icon(imageVector = imageVector, contentDescription = contentDescription)
    }
}

@Composable
fun OutlinedRoundedButton(
    text: String,
    onClick: () -> Unit,
    testTag: String,
    contentDescription: String,
    enabled: Boolean = true,
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black,
    disabledContainerColor: Color = Color.LightGray,
    disabledContentColor: Color = Color.Gray,
    borderColor: Color = Color.Gray,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) containerColor.copy(alpha = 0.7f) else containerColor

    Button(
        onClick = onClick,
        shape = CircleShape,
        enabled = enabled,
        border = BorderStroke(2.dp, borderColor),
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        ),
        modifier = Modifier
            .testTag(testTag)
            .semantics { this.contentDescription = contentDescription }
            .fillMaxWidth(),
    ) {
        Text(text)
    }
}

@Composable
fun ArrowBackButton(
    onClick: () -> Unit,
    arrowColor: Color = Color.Black,
) {
    IconButton(
        modifier = Modifier.size(24.dp),
        onClick = onClick,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Kembali",
            tint = arrowColor
        )
    }
}
