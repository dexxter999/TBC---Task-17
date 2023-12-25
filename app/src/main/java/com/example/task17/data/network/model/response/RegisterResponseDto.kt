package com.example.task17.data.network.model.response

import com.example.task17.domain.RegisterResponse

data class RegisterResponseDto(
    val id: Int,
    val token: String
) {
    fun toRegister() = RegisterResponse(id, token)
}