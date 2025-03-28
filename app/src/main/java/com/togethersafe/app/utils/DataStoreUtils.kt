package com.togethersafe.app.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.first

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "TogetherSafe")

private val MAP_LOCATION_KEY = stringPreferencesKey("map")
private val TOKEN_KEY = stringPreferencesKey("token")

suspend fun saveMapLocation(context: Context, location: Point) {
    val latitude = location.latitude()
    val longitude = location.longitude()

    context.datastore.edit { prefs ->
        prefs[MAP_LOCATION_KEY] = "$latitude,$longitude"
    }
}

suspend fun getMapLocation(context: Context): Point? {
    val prefs = context.datastore.data.first()
    val location = prefs[MAP_LOCATION_KEY]

    if (location != null) {
        val splitLocation = location.split(',')
        val latitude = splitLocation[0].toDouble()
        val longitude = splitLocation[1].toDouble()

        return Point.fromLngLat(longitude, latitude)
    }

    return null
}

suspend fun saveToken(context: Context, token: String) {
    context.datastore.edit { prefs ->
        prefs[TOKEN_KEY] = token
    }
}

suspend fun removeToken(context: Context) {
    context.datastore.edit { prefs ->
        prefs.remove(TOKEN_KEY)
    }
}

suspend fun getToken(context: Context): String? {
    val prefs = context.datastore.data.first()
    return prefs[TOKEN_KEY]
}
