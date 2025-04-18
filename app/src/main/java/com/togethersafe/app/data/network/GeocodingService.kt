package com.togethersafe.app.data.network

import com.togethersafe.app.data.dto.GeocodingResDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("geolocation/search?format=json")
    suspend fun searchLocation(@Query("q") query: String): List<GeocodingResDto>
}
