package com.togethersafe.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor() : ViewModel() {
    private var _onResult: (() -> Unit)? = null

    private val _isPermissionRequest = MutableStateFlow(false)
    val isPermissionRequest: StateFlow<Boolean> get() = _isPermissionRequest

    fun requestPermission(onResult: (() -> Unit)?) {
        _isPermissionRequest.value = true
        _onResult = onResult
    }

    fun completeRequestPermission(granted: Boolean) {
        _isPermissionRequest.value = false
        if (granted) _onResult?.invoke()
    }
}
