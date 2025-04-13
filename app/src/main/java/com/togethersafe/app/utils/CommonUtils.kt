package com.togethersafe.app.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.togethersafe.app.data.dto.ApiBadRequestDto
import com.togethersafe.app.data.dto.ApiErrorDto
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import kotlin.reflect.KClass

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

val gson = Gson()

inline fun <reified T> parseJson(
    json: String?,
    logClassName: String?,
): T? {
    return try {
        if (json.isNullOrBlank()) return null
        gson.fromJson(json, T::class.java)
    } catch (e: Exception) {
        logClassName?.let {
            Log.w(logClassName, "Parse Error: ${e.stackTraceToString()}")
        }
        null
    }
}

suspend fun handleApiError(
    className: KClass<*>,
    e: Exception,
    onError: ApiErrorCallback,
) {
    when (e) {
        is HttpException -> {
            val statusCode = e.code()
            val json = e.response()?.errorBody()?.string()

            val messages: List<String> = if (statusCode == 400) {
                parseJson<ApiBadRequestDto>(json, className.simpleName)?.messages ?: emptyList()
            } else {
                listOf(
                    parseJson<ApiErrorDto>(json, className.simpleName)?.message
                        ?: "Terjadi kesalahan."
                )
            }

            onError(statusCode, messages)
        }

        is IOException -> {
            Log.e(className.simpleName, "Network Error: ${e.localizedMessage}")
            onError(-1, listOf("Koneksi gagal, periksa jaringan anda."))
        }

        else -> {
            Log.e(className.simpleName, "Unknown Error: ${e.localizedMessage}")
            onError(-1, listOf("Terjadi kesalahan yang tidak diketahui."))
        }
    }
}
