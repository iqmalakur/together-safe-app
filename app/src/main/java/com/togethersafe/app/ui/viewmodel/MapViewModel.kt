package com.togethersafe.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.togethersafe.app.utils.MapConfig.ZOOM_DEFAULT
import com.togethersafe.app.utils.MapConfig.ZOOM_MAX
import com.togethersafe.app.utils.MapConfig.ZOOM_MIN
import com.togethersafe.app.utils.MapConfig.ZOOM_STEP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _zoomLevel = MutableStateFlow(ZOOM_DEFAULT)
    val zoomLevel: StateFlow<Double> get() = _zoomLevel

    fun zoomIn() {
        if (_zoomLevel.value < ZOOM_MAX) {
            _zoomLevel.value += ZOOM_STEP
        }
    }

    fun zoomOut() {
        if (_zoomLevel.value > ZOOM_MIN) {
            _zoomLevel.value -= ZOOM_STEP
        }
    }
}
