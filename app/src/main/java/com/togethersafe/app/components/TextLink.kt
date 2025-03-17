package com.togethersafe.app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TextLink(
    text: String,
    clickableText: String,
    onClick: () -> Unit,
) {
    Row {
        Text("$text ")
        Text(
            text = clickableText,
            color = Color.Blue,
            modifier = Modifier.clickable { onClick() }
        )
    }
}
