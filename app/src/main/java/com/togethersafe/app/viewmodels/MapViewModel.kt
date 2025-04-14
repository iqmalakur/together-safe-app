package com.togethersafe.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.togethersafe.app.constants.MapConstants.LATITUDE_DEFAULT
import com.togethersafe.app.constants.MapConstants.LONGITUDE_DEFAULT
import com.togethersafe.app.constants.MapConstants.ZOOM_DEFAULT
import com.togethersafe.app.constants.MapConstants.ZOOM_MAX
import com.togethersafe.app.constants.MapConstants.ZOOM_MIN
import com.togethersafe.app.constants.MapConstants.ZOOM_STEP
import com.togethersafe.app.utils.getMapLocation
import com.togethersafe.app.utils.saveMapLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel() {
    private var job: Job? = null

    private val _zoomLevel = MutableStateFlow(ZOOM_DEFAULT)
    private val _isLoadingLocation = MutableStateFlow(false)
    private val _isTracking = MutableStateFlow(false)
    private val _cameraPosition =
        MutableStateFlow(Point.fromLngLat(LONGITUDE_DEFAULT, LATITUDE_DEFAULT))
    private val _userPosition = MutableStateFlow<Point?>(null)
    private val _destination = MutableStateFlow<Point?>(null)

    val zoomLevel: StateFlow<Double> get() = _zoomLevel
    val isLoadingLocation: StateFlow<Boolean> get() = _isLoadingLocation
    val isTracking: StateFlow<Boolean> get() = _isTracking
    val cameraPosition: StateFlow<Point> get() = _cameraPosition
    val userPosition: StateFlow<Point?> get() = _userPosition
    val destination: StateFlow<Point?> get() = _destination

    init {
        viewModelScope.launch {
            val lastCameraPosition = getMapLocation(context)

            if (lastCameraPosition != null) {
                _cameraPosition.value = lastCameraPosition
            }
        }
    }

    fun setZoomLevel(zoomLevel: Double) {
        _zoomLevel.value = zoomLevel
    }

    fun zoomIn() {
        if (_zoomLevel.value < ZOOM_MAX) _zoomLevel.value += ZOOM_STEP
    }

    fun zoomOut() {
        if (_zoomLevel.value > ZOOM_MIN) _zoomLevel.value -= ZOOM_STEP
    }

    fun setLoadingLocation(isLoading: Boolean) {
        _isLoadingLocation.value = isLoading
    }

    fun startTracking() {
        _isLoadingLocation.value = true
        _isTracking.value = true
    }

    fun stopTracking() {
        _isTracking.value = false
    }

    fun setCameraPosition(latitude: Double, longitude: Double) {
        _cameraPosition.value = Point.fromLngLat(longitude, latitude)
    }

    fun setUserPosition(latitude: Double, longitude: Double) {
        _userPosition.value = Point.fromLngLat(longitude, latitude)
    }

    fun setDestination(destination: Point) {
        _destination.value = destination
    }

    fun cancelScheduledMapSave() {
        job?.cancel()
    }

    fun scheduleMapSave(position: Point) {
        job = viewModelScope.launch {
            delay(2000)
            saveMapLocation(context, position)
        }
    }
}
