package com.example.task17.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.task17.App
import com.example.task17.core.helper.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Application.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.USER_PREFERENCES)

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {
    @Singleton
    @Provides
    fun provideDataStore(): DataStore<Preferences> {
        return App.context.dataStore
    }
}