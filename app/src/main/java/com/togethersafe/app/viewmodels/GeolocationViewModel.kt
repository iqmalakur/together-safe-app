package com.togethersafe.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.togethersafe.app.data.dto.GeocodingResDto
import com.togethersafe.app.repositories.GeolocationRepository
import com.togethersafe.app.utils.ApiSuccessCallback
import com.togethersafe.app.utils.handleApiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeolocationViewModel @Inject constructor(private val repository: GeolocationRepository) :
    ViewModel() {

    private val _locationResult = MutableStateFlow<List<GeocodingResDto>>(emptyList())
    private val _routes = MutableStateFlow<List<List<Point>>>(emptyList())

    val locationResult: StateFlow<List<GeocodingResDto>> get() = _locationResult
    val routes: StateFlow<List<List<Point>>> get() = _routes

    fun search(query: String, onError: (String) -> Unit, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                _locationResult.value = repository.findLocation(query)
            } catch (e: Exception) {
                handleApiError(this::class, e) { _, errors ->
                    onError(errors[0])
                }
            }
            onComplete()
        }
    }

    fun fetchLocation(
        latitude: Double,
        longitude: Double,
        onError: (String) -> Unit,
        onComplete: () -> Unit,
        onSuccess: ApiSuccessCallback<GeocodingResDto>
    ) {
        viewModelScope.launch {
            try {
                onSuccess(repository.findLocationByLatLon(latitude, longitude))
            } catch (e: Exception) {
                handleApiError(this::class, e) { _, errors ->
                    onError(errors[0])
                }
            }
            onComplete()
        }
    }

    fun fetchSafeRoutes(
        startLocation: Point,
        endLocation: Point,
        onError: (String) -> Unit,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                _routes.value = repository.findSafeRoute(startLocation, endLocation)
            } catch (e: Exception) {
                handleApiError(this::class, e) { _, errors -> onError(errors[0]) }
            }
            onComplete()
        }
    }

    fun clearRoutes() {
        _routes.value = emptyList()
    }

}
