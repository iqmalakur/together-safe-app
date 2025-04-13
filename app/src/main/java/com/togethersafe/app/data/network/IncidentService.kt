package com.togethersafe.app.data.network

import com.togethersafe.app.data.dto.IncidentDetailResDto
import com.togethersafe.app.data.dto.IncidentResDto
import com.togethersafe.app.data.dto.ReportPreviewDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IncidentService {
    @GET("incident")
    suspend fun fetchIncidents(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String
    ): List<IncidentResDto>

    @GET("incident/{id}")
    suspend fun fetchDetailIncident(@Path("id") id: String): IncidentDetailResDto

    @GET("incident/{id}/reports")
    suspend fun fetchIncidentReports(@Path("id") id: String): List<ReportPreviewDto>
}
