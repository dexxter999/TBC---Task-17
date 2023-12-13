package com.example.task17.repository


import com.example.task17.network.AuthApi
import com.example.task17.network.model.LoginAndRegisterRequest

class AuthRepository(private val authApi: AuthApi) : BaseRepository() {

    suspend fun login(requestBody: LoginAndRegisterRequest) =
        apiCall { authApi.login(requestBody) }

    suspend fun register(requestBody: LoginAndRegisterRequest) =
        apiCall { authApi.register(requestBody) }

}
