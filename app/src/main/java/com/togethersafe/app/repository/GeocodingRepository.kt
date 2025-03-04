package com.togethersafe.app.repository

import com.togethersafe.app.data.model.GeocodingLocation
import com.togethersafe.app.data.network.GeocodingService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocodingRepository @Inject constructor(private val geocodingService: GeocodingService) {
    suspend fun findLocation(query: String): List<GeocodingLocation> {
        return geocodingService.searchLocation(query)
    }
}
