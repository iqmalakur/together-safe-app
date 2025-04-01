package com.togethersafe.app.views.register

import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.togethersafe.app.components.ErrorMessages
import com.togethersafe.app.components.InputTextField
import com.togethersafe.app.components.OutlinedRoundedButton
import com.togethersafe.app.components.PasswordTextField
import com.togethersafe.app.data.dto.RegisterReqDto
import com.togethersafe.app.data.model.DialogState
import com.togethersafe.app.navigation.LocalNavController
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.utils.uriToFile
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.AuthViewModel
import java.io.File

@Composable
fun RegisterForm() {
    val authViewModel: AuthViewModel = getViewModel()
    val appViewModel: AppViewModel = getViewModel()
    val context = LocalContext.current
    val navController = LocalNavController.current

    val errorMessages by authViewModel.registerErrors.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val successDialog = DialogState(
        title = "Pendaftaran Berhasil",
        message = "Akun Anda berhasil didaftarkan. Silakan login untuk melanjutkan.",
        onConfirm = { navController.popBackStack() }
    )

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

    InputTextField(
        label = "Nomor Telepon",
        value = phone,
        keyboardType = KeyboardType.Phone,
        onValueChange = { phone = it }
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
        onClick = {
            var profilePhoto: File? = null

            if (imageUri != null) {
                profilePhoto = uriToFile(context, imageUri!!)
            }

            authViewModel.register(
                RegisterReqDto(
                    name = name,
                    email = email,
                    password = password,
                    phone = phone,
                    profilePhoto = profilePhoto
                ),
                onSuccess = { appViewModel.setDialogState(successDialog) }
            )
        }
    )
}
