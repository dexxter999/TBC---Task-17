package com.example.task17.data.network.model.response

import com.example.task17.domain.LoginResponse

data class LoginResponseDto(
    val token: String
) {
    fun toLogin() = LoginResponse(token)
}