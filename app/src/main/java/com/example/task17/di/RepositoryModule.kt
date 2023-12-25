package com.example.task17.di

import com.example.task17.data.repository.AuthRepositoryImpl
import com.example.task17.domain.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindAuthRepository(repositoryImpl: AuthRepositoryImpl): AuthRepository
}