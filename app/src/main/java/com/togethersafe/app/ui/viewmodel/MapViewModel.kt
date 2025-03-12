package com.togethersafe.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.mapbox.geojson.Point
import com.togethersafe.app.utils.MapConfig.LATITUDE_DEFAULT
import com.togethersafe.app.utils.MapConfig.LONGITUDE_DEFAULT
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
    private val _isTracking = MutableStateFlow(false)
    private val _cameraPosition =
        MutableStateFlow(Point.fromLngLat(LONGITUDE_DEFAULT, LATITUDE_DEFAULT))
    private val _userPosition = MutableStateFlow<Point?>(null)
    private val _destination = MutableStateFlow<Point?>(null)

    val zoomLevel: StateFlow<Double> get() = _zoomLevel
    val isTracking: StateFlow<Boolean> get() = _isTracking
    val cameraPosition: StateFlow<Point> get() = _cameraPosition
    val userPosition: StateFlow<Point?> get() = _userPosition
    val destination: StateFlow<Point?> get() = _destination

    fun setZoomLevel(zoomLevel: Double) { _zoomLevel.value = zoomLevel }

    fun zoomIn() {
        if (_zoomLevel.value < ZOOM_MAX) _zoomLevel.value += ZOOM_STEP
    }

    fun zoomOut() {
        if (_zoomLevel.value > ZOOM_MIN) _zoomLevel.value -= ZOOM_STEP
    }

    fun startTracking() {
        _isTracking.value = true
    }

    fun stopTracking() { _isTracking.value = false }

    fun setCameraPosition(latitude: Double, longitude: Double) {
        _cameraPosition.value = Point.fromLngLat(longitude, latitude)
    }

    fun setUserPosition(latitude: Double, longitude: Double) {
        _userPosition.value = Point.fromLngLat(longitude, latitude)
    }

    fun setDestination(destination: Point) {
        _destination.value = destination
    }
}
