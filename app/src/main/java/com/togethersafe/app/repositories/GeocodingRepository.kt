package com.togethersafe.app.repositories

import com.togethersafe.app.data.dto.GeocodingResDto
import com.togethersafe.app.data.network.GeocodingService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocodingRepository @Inject constructor(private val geocodingService: GeocodingService) {
    suspend fun findLocation(query: String): List<GeocodingResDto> {
        return geocodingService.searchLocation(query)
    }
}
