package com.togethersafe.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.model.GeocodingLocation
import com.togethersafe.app.repositories.GeocodingRepository
import com.togethersafe.app.utils.handleApiError
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

    fun search(query: String, onError: (String) -> Unit, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                _locationResult.value = geocodingRepository.findLocation(query)
            } catch (e: Exception) {
                handleApiError(this::class, e) { _, errors ->
                    onError(errors[0])
                }
            }
            onComplete()
        }
    }

}
