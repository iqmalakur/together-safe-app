package com.togethersafe.app.views.register

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.togethersafe.app.components.AuthScreenHeader
import com.togethersafe.app.components.TextLink
import com.togethersafe.app.navigation.LocalNavController

@Composable
fun RegisterScreen() {
    val navController = LocalNavController.current
    val focusManager = LocalFocusManager.current

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) {
        AuthScreenHeader(
            headerText = "Buat Akun Baru",
            descriptionText = "Masukkan informasi yang diperlukan untuk membuat akun dan mulai " +
                    "menggunakan fitur lengkap aplikasi Together Safe",
            onClick = { navController.popBackStack() }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
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
