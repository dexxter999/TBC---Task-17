package com.example.task17.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    val getEmail: Flow<String> = dataStore.data
        .map {
            it[KEY_EMAIL] ?: ""
        }

    val getToken: Flow<String> = dataStore.data
        .map { it[KEY_TOKEN] ?: "" }

    suspend fun saveSession(email: String, token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_EMAIL] = email
            preferences[KEY_TOKEN] = token
        }
    }


    suspend fun getSavedEmail(): String {
        return getEmail.first()
    }

    suspend fun getSavedToken(): String {
        return getToken.first()
    }

    suspend fun clearSession() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_TOKEN = stringPreferencesKey("token")
    }
}