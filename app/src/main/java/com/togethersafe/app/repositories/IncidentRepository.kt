package com.togethersafe.app.repositories

import com.togethersafe.app.data.dto.IncidentDetailResDto
import com.togethersafe.app.data.dto.IncidentResDto
import com.togethersafe.app.data.dto.ReportPreviewDto
import com.togethersafe.app.data.network.IncidentService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncidentRepository @Inject constructor(private val service: IncidentService) {
    suspend fun getIncidents(latitude: Double, longitude: Double): List<IncidentResDto> {
        return service.fetchIncidents(latitude.toString(), longitude.toString())
    }

    suspend fun getDetailIncident(id: String): IncidentDetailResDto {
        return service.fetchDetailIncident(id)
    }

    suspend fun getIncidentReports(id: String): List<ReportPreviewDto> {
        return service.fetchIncidentReports(id)
    }
}
