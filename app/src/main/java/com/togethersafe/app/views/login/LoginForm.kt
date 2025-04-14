package com.togethersafe.app.views.login

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.togethersafe.app.components.ErrorMessages
import com.togethersafe.app.components.InputTextField
import com.togethersafe.app.components.OutlinedRoundedButton
import com.togethersafe.app.components.PasswordTextField
import com.togethersafe.app.navigation.LocalNavController
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.AuthViewModel

@Composable
fun LoginForm() {
    val appViewModel: AppViewModel = getViewModel()
    val authViewModel: AuthViewModel = getViewModel()

    var errorMessages by remember { mutableStateOf<List<String>>(emptyList()) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val navController = LocalNavController.current

    InputTextField(
        label = "Email",
        value = email,
        keyboardType = KeyboardType.Email,
        onValueChange = { email = it }
    )

    Spacer(modifier = Modifier.height(8.dp))

    PasswordTextField(
        label = "Password",
        value = password,
        onValueChange = { password = it },
    )

    Spacer(modifier = Modifier.height(16.dp))

    ErrorMessages(errorMessages)

    Spacer(modifier = Modifier.height(16.dp))

    Text("atau", fontSize = 14.sp, color = Color.Gray)

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedRoundedButton(
        text = "Masuk dengan google",
        testTag = "GoogleLoginButton",
        contentDescription = "Tombol masuk dengan google",
        onClick = { /* TODO: handle login with google */ },
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedRoundedButton(
        text = "Masuk",
        testTag = "LoginSubmitButton",
        contentDescription = "Tombol aksi login",
        containerColor = Color.Black,
        contentColor = Color.White,
        onClick = {
            authViewModel.login(
                email = email,
                password = password,
                onError = { _, messages -> errorMessages = messages }
            ) {
                appViewModel.setToastMessage("Login berhasil!")
                navController.popBackStack()
            }
        }
    )
}
