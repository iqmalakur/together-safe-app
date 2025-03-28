package com.togethersafe.app.views.register

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SuccessDialog(onContinue: () -> Unit) {
    AlertDialog(
        onDismissRequest = onContinue,
        title = { Text("Pendaftaran Berhasil") },
        text = { Text("Akun Anda berhasil didaftarkan. Silakan login untuk melanjutkan.") },
        confirmButton = {
            Button(onClick = onContinue) {
                Text("OK")
            }
        }
    )
}