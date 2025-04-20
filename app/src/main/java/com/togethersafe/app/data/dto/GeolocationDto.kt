package com.togethersafe.app.data.dto

data class GeocodingResDto(
    val name: String,
    val fullName: String,
    val latitude: Double,
    val longitude: Double,
)

data class SafeRouteResDto(
    val routes: List<List<List<Double>>>
)
