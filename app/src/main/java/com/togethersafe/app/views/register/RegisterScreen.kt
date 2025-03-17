package com.togethersafe.app.views.register

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.togethersafe.app.components.FormScreenHeader
import com.togethersafe.app.components.TextLink
import com.togethersafe.app.navigation.LocalNavController

@Composable
fun RegisterScreen() {
    val navController = LocalNavController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) {
        FormScreenHeader(
            headerText = "Buat Akun Baru",
            descriptionText = "Masukkan informasi yang diperlukan untuk membuat akun dan mulai " +
                    "menggunakan fitur lengkap aplikasi Together Safe",
            onClick = { navController.popBackStack() }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            RegisterForm()

            Spacer(modifier = Modifier.height(16.dp))

            TextLink(
                text = "Sudah punya akun?",
                clickableText = "Masuk ke akun saya",
                onClick = { navController.popBackStack() }
            )
        }
    }
}
