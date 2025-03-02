package com.togethersafe.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.model.Incident
import com.togethersafe.app.repository.IncidentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class IncidentViewModel(private val repository: IncidentRepository): ViewModel() {
    private val _incidents = MutableStateFlow<List<Incident>>(emptyList())
    val incidents: StateFlow<List<Incident>> get() = _incidents

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun loadIncidents() {
        viewModelScope.launch {
            try {
                _incidents.value = repository.getIncidents()
            } catch (e: Exception) {
                _error.value = "Terjadi keasalahn saat mengambil data"
            }
        }
    }
}
