package com.togethersafe.app.repositories

import com.mapbox.geojson.Point
import com.togethersafe.app.data.dto.GeocodingResDto
import com.togethersafe.app.data.network.GeolocationService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeolocationRepository @Inject constructor(private val geolocationService: GeolocationService) {
    suspend fun findLocation(query: String): List<GeocodingResDto> {
        return geolocationService.searchLocation(query)
    }

    suspend fun findSafeRoute(startLocation: Point, endLocation: Point): List<List<Point>> {
        val startLatLon = "${startLocation.latitude()},${startLocation.longitude()}"
        val endLatLon = "${endLocation.latitude()},${endLocation.longitude()}"

        val result = geolocationService.findSafeRoute(startLatLon, endLatLon)
        return result.routes.map { route -> route.map { Point.fromLngLat(it[0], it[1]) } }
    }
}
