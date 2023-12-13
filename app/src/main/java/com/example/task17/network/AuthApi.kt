package com.example.task17.network

import com.example.task17.network.model.LoginAndRegisterRequest
import com.example.task17.network.model.LoginResponse
import com.example.task17.network.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("register")
    suspend fun register(@Body body: LoginAndRegisterRequest): Response<RegisterResponse>

    @POST("login")
    suspend fun login(@Body body: LoginAndRegisterRequest): Response<LoginResponse>
}