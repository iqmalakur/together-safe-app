package com.togethersafe.app.data.network

import com.togethersafe.app.data.dto.GeocodingResDto
import com.togethersafe.app.data.dto.SafeRouteResDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeolocationService {
    @GET("geolocation/search?format=json")
    suspend fun searchLocation(@Query("q") query: String): List<GeocodingResDto>

    @GET("geolocation/safe-route")
    suspend fun findSafeRoute(
        @Query("startLatLon") startLatLon: String,
        @Query("endLatLon") endLatLon: String,
    ): SafeRouteResDto
}
