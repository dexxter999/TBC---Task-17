package com.example.task17.presentation.screens.fragments.loginfragment

sealed class LoginValidationResult {
    data object Success : LoginValidationResult()
    data class Error(val message: String) : LoginValidationResult()
}