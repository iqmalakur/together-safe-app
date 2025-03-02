package com.togethersafe.app.repository

import com.togethersafe.app.data.model.Incident
import com.togethersafe.app.data.network.ApiService

class IncidentRepository(private val apiService: ApiService) {
    suspend fun getIncidents(): List<Incident> {
        return apiService.getIncidents()
    }
}
