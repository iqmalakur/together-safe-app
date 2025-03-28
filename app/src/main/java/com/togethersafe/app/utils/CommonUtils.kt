package com.togethersafe.app.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import java.io.File

@Composable
fun getActivity() = LocalActivity.current as ComponentActivity

@Composable
inline fun <reified VM : ViewModel> getViewModel() = hiltViewModel<VM>(getActivity())

fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        tempFile
    } catch (e: Exception) {
        Log.e("uriToFile", e.stackTraceToString())
        null
    }
}
