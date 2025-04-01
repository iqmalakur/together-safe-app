package com.togethersafe.app.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.dto.AuthResDto
import com.togethersafe.app.data.dto.RegisterReqDto
import com.togethersafe.app.data.model.User
import com.togethersafe.app.repositories.AuthRepository
import com.togethersafe.app.utils.getToken
import com.togethersafe.app.utils.getUser
import com.togethersafe.app.utils.removeUser
import com.togethersafe.app.utils.saveUser
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

    init {
        viewModelScope.launch {
            val user = getUser(context)
            if (user != null) {
                _user.value = user
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: (token: String) -> Unit
    ) {
        viewModelScope.launch {
            handleRequest(
                onSuccess = onSuccess,
                onError = { errors, _ -> _loginErrors.value = errors },
            ) {
                val result = repository.login(email, password)
                saveUser(context, result)
                result
            }
        }
    }

    fun register(registerReqDto: RegisterReqDto, onSuccess: () -> Unit) {
        viewModelScope.launch {
            handleRequest(
                isHandleResult = false,
                onSuccess = { onSuccess() },
                onError = { errors, _ -> _registerErrors.value = errors },
            ) { repository.register(registerReqDto) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _user.value = null
            removeUser(context)
        }
    }

    fun verifyToken() {
        viewModelScope.launch {
            val token = getToken(context)

            if (token != null) {
                handleRequest(
                    onError = { _, statusCode ->
                        if (statusCode == 401) {
                            removeUser(context)
                            _user.value = null
                        }
                    }
                ) {
                    repository.validateToken(token)
                }
            }
        }
    }

    private suspend fun handleRequest(
        onSuccess: (token: String) -> Unit = {},
        onError: suspend (errors: List<String>, statusCode: Int) -> Unit = { _, _ -> },
        isHandleResult: Boolean = true,
        action: suspend () -> AuthResDto
    ) {
        try {
            val result = action()
            onSuccess(result.token)

            if (isHandleResult) {
                handleResult(result)
            }

            _loginErrors.value = emptyList()
            _registerErrors.value = emptyList()
        } catch (e: HttpException) {
            logError(e)
            onError(handleError(e), e.code())
        } catch (e: Exception) {
            logError(e)
            onError(listOf("Terjadi keasalahan tidak terduga"), 500)
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
            try {
                val json = JSONObject(it)

                if (errorCode < 500) {
                    val messageArray = json.getJSONArray("message")
                    errorMessages = List(messageArray.length()) { i -> messageArray.getString(i) }
                } else {
                    errorMessages = listOf(json.getString("message"))
                }
            } catch (e: Exception) {
                logError(e)
                errorMessages = listOf("Terjadi keasalahan tidak terduga")
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