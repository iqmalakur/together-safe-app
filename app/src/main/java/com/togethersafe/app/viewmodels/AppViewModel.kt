package com.togethersafe.app.viewmodels

import androidx.lifecycle.ViewModel
import com.togethersafe.app.data.model.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {
    private var _onLocationPermissionResult: (() -> Unit)? = null
    private val _isPermissionRequest = MutableStateFlow(false)
    private val _toastMessage = MutableStateFlow("")
    private val _dialogState = MutableStateFlow<DialogState?>(null)
    private val _isMenuOpen = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    private val _isLoadIncident = MutableStateFlow(true)

    val isPermissionRequest: StateFlow<Boolean> get() = _isPermissionRequest
    val toastMessage: StateFlow<String> get() = _toastMessage
    val dialogState: StateFlow<DialogState?> get() = _dialogState
    val isMenuOpen: StateFlow<Boolean> get() = _isMenuOpen
    val isLoading: StateFlow<Boolean> get() = _isLoading
    val isLoadIncident: StateFlow<Boolean> get() = _isLoadIncident

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

    fun setDialogState(dialogState: DialogState?) {
        _dialogState.value = dialogState
    }

    fun setMenuOpen(isOpen: Boolean) {
        _isMenuOpen.value = isOpen
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun setLoadIncident(isLoadIncident: Boolean) {
        _isLoadIncident.value = isLoadIncident
    }
}
