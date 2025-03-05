package com.togethersafe.app.data.model

data class GeocodingLocation(
    val place_id: Long,
    val osm_id: Long,
    val lat: String,
    val lon: String,
    val name: String,
    val display_name: String,
)
