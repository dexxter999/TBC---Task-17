package com.example.task17.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepository(private val context: Context) {

    val getEmail: Flow<String> = context.dataStore.data
        .map {
            it[KEY_EMAIL] ?: ""
        }

    suspend fun saveSession(email: String, token: String, rememberMe: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_EMAIL] = email
            preferences[KEY_TOKEN] = token
            preferences[KEY_REMEMBER_ME] = rememberMe.toString()
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it.clear()
        }
    }

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "TOKEN")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_REMEMBER_ME = stringPreferencesKey("rememberMe")
    }
}