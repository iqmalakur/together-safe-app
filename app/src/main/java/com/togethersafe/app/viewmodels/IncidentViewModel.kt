package com.togethersafe.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.togethersafe.app.data.dto.IncidentDetailResDto
import com.togethersafe.app.data.dto.IncidentResDto
import com.togethersafe.app.data.dto.ReportPreviewDto
import com.togethersafe.app.repositories.IncidentRepository
import com.togethersafe.app.utils.ApiErrorCallback
import com.togethersafe.app.utils.ApiSuccessCallback
import com.togethersafe.app.utils.handleApiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncidentViewModel @Inject constructor(private val repository: IncidentRepository) :
    ViewModel() {
    private val _incidents = MutableStateFlow<List<IncidentResDto>>(emptyList())
    private val _selectedIncident = MutableStateFlow<IncidentDetailResDto?>(null)

    val incidents: StateFlow<List<IncidentResDto>> get() = _incidents
    val selectedIncident: StateFlow<IncidentDetailResDto?> = _selectedIncident

    fun loadIncidents(location: Point, onError: ApiErrorCallback) {
        viewModelScope.launch {
            try {
                _incidents.value =
                    repository.getIncidents(location.latitude(), location.longitude())
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
        }
    }

    fun fetchIncidentById(id: String, onError: ApiErrorCallback) {
        viewModelScope.launch {
            try {
                _selectedIncident.value = repository.getDetailIncident(id)
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
        }
    }

    fun fetchIncidentReports(
        incidentId: String,
        onSuccess: ApiSuccessCallback<List<ReportPreviewDto>>,
        onError: ApiErrorCallback
    ) {
        viewModelScope.launch {
            try {
                onSuccess(repository.getIncidentReports(incidentId))
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
        }
    }

    fun clearSelectedIncident() {
        _selectedIncident.value = null
    }
}
