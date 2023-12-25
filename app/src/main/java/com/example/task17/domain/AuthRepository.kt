package com.example.task17.domain

import com.example.task17.data.network.Resource
import com.example.task17.data.network.model.request.LoginAndRegisterRequest
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(requestBody: LoginAndRegisterRequest): Flow<Resource<LoginResponse>>

    suspend fun register(requestBody: LoginAndRegisterRequest): Flow<Resource<RegisterResponse>>

}