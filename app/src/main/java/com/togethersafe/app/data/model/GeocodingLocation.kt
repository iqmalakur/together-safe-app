package com.togethersafe.app.data.model

data class GeocodingLocation(
    val place_id: Int,
    val osm_id: Int,
    val lat: String,
    val lon: String,
    val display_name: String,
)
