package com.togethersafe.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ErrorMessages(errors: List<String>) {
    if (errors.isNotEmpty()) {
        Column(modifier = Modifier.padding(8.dp)) {
            errors.forEach { error ->
                Text(
                    text = "• $error",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
