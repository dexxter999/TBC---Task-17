package com.example.task17.data.network.model.response

import com.example.task17.domain.LoginAndRegister

data class LoginAndRegisterDto(
    val email: String,
    val password: String
) {
    fun toLoginAndRegister() = LoginAndRegister(email, password)



    }