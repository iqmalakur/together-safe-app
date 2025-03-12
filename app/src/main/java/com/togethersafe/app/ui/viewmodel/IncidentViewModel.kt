package com.togethersafe.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.model.Incident
import com.togethersafe.app.repository.IncidentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncidentViewModel @Inject constructor(private val repository: IncidentRepository): ViewModel() {
    private val _incidents = MutableStateFlow<List<Incident>>(emptyList())
    private val _selectedIncident = MutableStateFlow<Incident?>(null)
    private val _error = MutableStateFlow<String?>(null)

    val incidents: StateFlow<List<Incident>> get() = _incidents
    val selectedIncident: StateFlow<Incident?> get() = _selectedIncident
    val error: StateFlow<String?> get() = _error

    fun loadIncidents() {
        viewModelScope.launch {
            try {
                _incidents.value = repository.getIncidents()
            } catch (e: Exception) {
                Log.e("IncidentViewModel", e.toString())
                _error.value = "Terjadi keasalahn saat mengambil data"
            }
        }
    }

    fun setSelectedIncident(incident: Incident?) {
        _selectedIncident.value = incident
    }
}
