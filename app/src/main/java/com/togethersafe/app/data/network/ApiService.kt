package com.togethersafe.app.data.network

import com.togethersafe.app.data.model.Incident
import retrofit2.http.GET

interface ApiService {
    @GET("incident")
    suspend fun getIncidents(): List<Incident>
}
