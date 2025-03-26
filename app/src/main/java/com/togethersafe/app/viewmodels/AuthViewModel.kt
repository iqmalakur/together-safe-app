package com.togethersafe.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.dto.AuthResDto
import com.togethersafe.app.data.model.User
import com.togethersafe.app.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    private val _token = MutableStateFlow("")
    private val _loginErrors = MutableStateFlow<List<String>>(emptyList())

    val user: StateFlow<User?> get() = _user
    val token: StateFlow<String> get() = _token
    val loginErrors: StateFlow<List<String>> get() = _loginErrors

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            handleRequest(onSuccess) {
                repository.login(email, password)
            }
        }
    }

    private suspend fun handleRequest(onSuccess: () -> Unit, action: suspend () -> AuthResDto) {
        try {
            handleResult(action())
            onSuccess()
        } catch (e: HttpException) {
            logError(e)
            _loginErrors.value = handleError(e)
        } catch (e: Exception) {
            logError(e)
            _loginErrors.value = listOf("Terjadi keasalahan tidak terduga")
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
        _token.value = result.token
        _user.value = User(
            name = result.name,
            email = result.email,
            profilePhoto = result.profilePhoto
        )
    }
}