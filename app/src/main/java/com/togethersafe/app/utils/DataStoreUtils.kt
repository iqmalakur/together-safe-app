package com.togethersafe.app.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.mapbox.geojson.Point
import com.togethersafe.app.data.dto.AuthResDto
import com.togethersafe.app.data.model.User
import kotlinx.coroutines.flow.first
import org.json.JSONObject

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "TogetherSafe")

private val MAP_LOCATION_KEY = stringPreferencesKey("map")
private val FILTER_KEY = stringPreferencesKey("filter")
private val USER_PROFILE_KEY = stringPreferencesKey("user_profile")
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

suspend fun getToken(context: Context): String? {
    val prefs = context.datastore.data.first()
    val encrypted = prefs[TOKEN_KEY] ?: return null
    return SecurityUtils.decrypt(encrypted, KeyStoreAlias.TOKEN_KEY)
}

suspend fun getUser(context: Context): User? {
    val prefs = context.datastore.data.first()
    val encrypted = prefs[USER_PROFILE_KEY] ?: return null
    val decrypted = SecurityUtils.decrypt(encrypted, KeyStoreAlias.PROFILE_KEY)

    val json = JSONObject(decrypted)
    return User(
        name = json.getString("name"),
        email = json.getString("email"),
        profilePhoto = json.optString("profilePhoto", "").ifEmpty { null }
    )
}

suspend fun saveUser(context: Context, result: AuthResDto) {
    val userJson = JSONObject().apply {
        put("name", result.name)
        put("email", result.email)
        put("profilePhoto", result.profilePhoto)
    }.toString()

    val encryptedUser = SecurityUtils.encrypt(userJson, KeyStoreAlias.PROFILE_KEY)
    val encryptedToken = SecurityUtils.encrypt(result.token, KeyStoreAlias.TOKEN_KEY)

    context.datastore.edit { prefs ->
        prefs[USER_PROFILE_KEY] = encryptedUser
        prefs[TOKEN_KEY] = encryptedToken
    }
}

suspend fun removeUser(context: Context) {
    context.datastore.edit { prefs ->
        prefs.remove(USER_PROFILE_KEY)
        prefs.remove(TOKEN_KEY)
    }
}

suspend fun saveIncidentFilter(context: Context, filterMap: Map<String, Boolean>) {
    val json = gson.toJson(filterMap)
    context.datastore.edit { prefs ->
        prefs[FILTER_KEY] = json
    }
}

@Suppress("UNCHECKED_CAST")
suspend fun getIncidentFilter(context: Context): Map<String, Boolean>? {
    val prefs = context.datastore.data.first()
    val json = prefs[FILTER_KEY]
    return json?.let {
        gson.fromJson(it, Map::class.java) as Map<String, Boolean>
    }
}

