package com.togethersafe.app.data.model

import com.mapbox.geojson.Point

data class GeocodingLocation(
    val lat: String,
    val lon: String,
    val name: String,
    val display_name: String,
) {

    fun getLocationPoint(): Point {
        val latitude = lat.toDouble()
        val longitude = lon.toDouble()
        return Point.fromLngLat(longitude, latitude)
    }

}
