package com.togethersafe.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.togethersafe.app.data.dto.CategoryResDto
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
    private val _categories = MutableStateFlow<List<CategoryResDto>>(emptyList())

    val incidents: StateFlow<List<IncidentResDto>> get() = _incidents
    val selectedIncident: StateFlow<IncidentDetailResDto?> get() = _selectedIncident
    val categories: StateFlow<List<CategoryResDto>> get() = _categories

    init {
        viewModelScope.launch {
            try {
                _categories.value = repository.getIncidentCategories()
            } catch (e: Exception) {
                handleApiError(this::class, e) { _, _ -> }
            }
        }
    }

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

    fun fetchIncidentById(id: String, onError: ApiErrorCallback, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                _selectedIncident.value = repository.getDetailIncident(id)
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

    fun fetchIncidentReports(
        incidentId: String,
        onError: ApiErrorCallback,
        onComplete: () -> Unit,
        onSuccess: ApiSuccessCallback<List<ReportPreviewDto>>,
    ) {
        viewModelScope.launch {
            try {
                onSuccess(repository.getIncidentReports(incidentId))
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

    fun clearSelectedIncident() {
        _selectedIncident.value = null
    }
}
