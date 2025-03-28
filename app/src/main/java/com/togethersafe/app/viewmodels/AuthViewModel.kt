package com.togethersafe.app.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.dto.AuthResDto
import com.togethersafe.app.data.dto.RegisterReqDto
import com.togethersafe.app.data.model.User
import com.togethersafe.app.repositories.AuthRepository
import com.togethersafe.app.utils.removeToken
import com.togethersafe.app.utils.saveToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    private val _loginErrors = MutableStateFlow<List<String>>(emptyList())
    private val _registerErrors = MutableStateFlow<List<String>>(emptyList())

    val user: StateFlow<User?> get() = _user
    val loginErrors: StateFlow<List<String>> get() = _loginErrors
    val registerErrors: StateFlow<List<String>> get() = _registerErrors

    fun login(
        email: String,
        password: String,
        onSuccess: (token: String) -> Unit
    ) {
        viewModelScope.launch {
            handleRequest(
                onSuccess = onSuccess,
                onError = { _loginErrors.value = it },
            ) {
                val result = repository.login(email, password)

                saveToken(context, result.token)
                handleResult(result)

                result
            }
        }
    }

    fun register(registerReqDto: RegisterReqDto, onSuccess: () -> Unit) {
        viewModelScope.launch {
            handleRequest(
                onSuccess = { onSuccess() },
                onError = { _registerErrors.value = it },
            ) { repository.register(registerReqDto) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _user.value = null
            removeToken(context)
        }
    }

    fun verifyToken(token: String) {
        viewModelScope.launch {
            handleRequest {
                repository.validateToken(token)
            }
        }
    }

    private suspend fun handleRequest(
        onSuccess: (token: String) -> Unit = {},
        onError: (errors: List<String>) -> Unit = {},
        action: suspend () -> AuthResDto
    ) {
        try {
            val result = action()
            onSuccess(result.token)
            _loginErrors.value = emptyList()
            _registerErrors.value = emptyList()
        } catch (e: HttpException) {
            logError(e)
            onError(handleError(e))
        } catch (e: Exception) {
            logError(e)
            onError(listOf("Terjadi keasalahan tidak terduga"))
        }
    }

    private fun logError(e: Exception) {
        Log.e(this::class.java.simpleName, e.toString())
    }

    private fun handleError(e: HttpException): List<String> {
        val errorCode = e.code()
        var errorMessages: List<String> = emptyList()

        val errorBody = e.response()?.errorBody()?.string()
        errorBody?.let {
            val json = JSONObject(it)

            if (errorCode < 500) {
                val messageArray = json.getJSONArray("message")
                errorMessages = List(messageArray.length()) { i -> messageArray.getString(i) }
            } else {
                errorMessages = listOf(json.getString("message"))
            }
        }

        return errorMessages
    }

    private fun handleResult(result: AuthResDto) {
        _user.value = User(
            name = result.name,
            email = result.email,
            profilePhoto = result.profilePhoto
        )
    }
}