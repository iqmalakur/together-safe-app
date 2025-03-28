package com.togethersafe.app.views.register

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.togethersafe.app.components.CameraLauncher
import com.togethersafe.app.components.GalleryLauncher
import com.togethersafe.app.components.InputTextField
import com.togethersafe.app.components.OutlinedRoundedButton
import com.togethersafe.app.components.PasswordTextField
import com.togethersafe.app.components.UserProfile

@Composable
fun RegisterForm() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    ProfileImagePicker(imageUri = imageUri, onImageSelected = { imageUri = it })

    Spacer(modifier = Modifier.height(16.dp))

    InputTextField(
        label = "Nama",
        value = name,
        onValueChange = { name = it }
    )

    Spacer(modifier = Modifier.height(8.dp))

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

    Text("atau", fontSize = 14.sp, color = Color.Gray)

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedRoundedButton(
        text = "Daftar dengan google",
        testTag = "GoogleRegisterButton",
        contentDescription = "Tombol daftar dengan google",
        onClick = { /* TODO: handle register with google */ },
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedRoundedButton(
        text = "Buat Akun",
        testTag = "RegisterSubmitButton",
        contentDescription = "Tombol aksi registrasi akun",
        containerColor = Color.Black,
        contentColor = Color.White,
        onClick = { /* TODO: handle register */ }
    )
}

@Composable
fun ProfileImagePicker(
    imageUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        UserProfile(imageUri, 100.dp)

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            GalleryLauncher(
                onResult = { uri -> onImageSelected(uri) }
            ) { launcher ->
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Dari Galeri")
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            CameraLauncher(
                onResult = { uri -> onImageSelected(uri) }
            ) { startLauncher ->
                Button(onClick = { startLauncher() }) {
                    Text("Ambil Foto")
                }
            }
        }
    }
}