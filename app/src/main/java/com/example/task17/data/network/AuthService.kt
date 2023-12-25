package com.example.task17.data.network

import com.example.task17.core.helper.Constants
import com.example.task17.data.network.model.request.LoginAndRegisterRequest
import com.example.task17.data.network.model.response.LoginResponseDto
import com.example.task17.data.network.model.response.RegisterResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST(Constants.END_POINT_REGISTER)
    suspend fun register(
        @Body loginAndRegisterRequest: LoginAndRegisterRequest
    ): Response<RegisterResponseDto>

    @POST(Constants.END_POINT_LOGIN)
    suspend fun login(
        @Body loginAndRegisterRequest: LoginAndRegisterRequest
    ): Response<LoginResponseDto>
}