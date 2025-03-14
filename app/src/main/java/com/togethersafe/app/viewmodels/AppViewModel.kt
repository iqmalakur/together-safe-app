package com.togethersafe.app.viewmodels

import androidx.lifecycle.ViewModel
import com.togethersafe.app.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {
    private var _onLocationPermissionResult: (() -> Unit)? = null
    private val _isPermissionRequest = MutableStateFlow(false)
    private val _toastMessage = MutableStateFlow("")
    private val _isMenuOpen = MutableStateFlow(false)
    private val _user = MutableStateFlow<User?>(null)

    val isPermissionRequest: StateFlow<Boolean> get() = _isPermissionRequest
    val toastMessage: StateFlow<String> get() = _toastMessage
    val isMenuOpen: StateFlow<Boolean> get() = _isMenuOpen
    val user: StateFlow<User?> get() = _user

    fun requestPermission(onResult: (() -> Unit)?) {
        _isPermissionRequest.value = true
        _onLocationPermissionResult = onResult
    }

    fun completeRequestPermission(granted: Boolean) {
        _isPermissionRequest.value = false
        if (granted) _onLocationPermissionResult?.invoke()
    }

    fun setToastMessage(message: String) {
        _toastMessage.value = message
    }

    fun setMenuOpen(isOpen: Boolean) {
        _isMenuOpen.value = isOpen
    }

    fun setUser(user: User?) {
        _user.value = user
    }
}
