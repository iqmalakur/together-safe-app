package com.togethersafe.app.repositories

import com.togethersafe.app.data.model.Incident
import com.togethersafe.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncidentRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getIncidents(): List<Incident> {
        return apiService.fetchIncidents()
    }
}
