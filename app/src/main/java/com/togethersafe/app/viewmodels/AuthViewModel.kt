package com.togethersafe.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.dto.RegisterReqDto
import com.togethersafe.app.data.model.User
import com.togethersafe.app.repositories.AuthRepository
import com.togethersafe.app.utils.ApiErrorCallback
import com.togethersafe.app.utils.getToken
import com.togethersafe.app.utils.getUser
import com.togethersafe.app.utils.handleApiError
import com.togethersafe.app.utils.removeUser
import com.togethersafe.app.utils.saveUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    init {
        viewModelScope.launch {
            getUser(context)?.let { _user.value = it }
        }
    }

    fun validateToken() {
        viewModelScope.launch {
            getToken(context)?.let { token ->
                try {
                    val result = repository.validateToken(token)
                    _user.value = User(
                        name = result.name,
                        email = result.email,
                        profilePhoto = result.profilePhoto,
                    )
                } catch (e: Exception) {
                    handleApiError(this::class, e) { status, _ ->
                        if (status == 401) {
                            removeUser(context)
                            _user.value = null
                        }
                    }
                }
            }
        }
    }

    fun login(email: String, password: String, onError: ApiErrorCallback, onComplete: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.login(email, password)

                _user.value = User(
                    name = result.name,
                    email = result.email,
                    profilePhoto = result.profilePhoto,
                )

                saveUser(context, result)
                onSuccess()
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

    fun register(registerReqDto: RegisterReqDto, onError: ApiErrorCallback, onComplete: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.register(registerReqDto)
                onSuccess()
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

    fun logout() {
        viewModelScope.launch {
            _user.value = null
            removeUser(context)
        }
    }
}
