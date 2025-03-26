package com.togethersafe.app.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "TogetherSafe")

private val TOKEN_KEY = stringPreferencesKey("token")

suspend fun saveToken(context: Context, token: String) {
    context.datastore.edit { prefs ->
        prefs[TOKEN_KEY] = token
    }
}

suspend fun getToken(context: Context): String? {
    val prefs = context.datastore.data.first()
    return prefs[TOKEN_KEY]
}
