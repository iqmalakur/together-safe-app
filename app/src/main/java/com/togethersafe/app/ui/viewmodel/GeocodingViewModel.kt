package com.togethersafe.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.model.GeocodingLocation
import com.togethersafe.app.repository.GeocodingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeocodingViewModel @Inject constructor(private val geocodingRepository: GeocodingRepository) :
    ViewModel() {

    private val _locationResult = MutableStateFlow<List<GeocodingLocation>>(emptyList())
    val locationResult: StateFlow<List<GeocodingLocation>> get() = _locationResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun search(query: String) {
        viewModelScope.launch {
            try {
                _locationResult.value = geocodingRepository.findLocation(query)
            } catch (e: Exception) {
                Log.e("GeocodingViewModel", e.toString())
                _error.value = "Terjadi keasalahn saat mencari lokasi"
            }
        }
    }

}
