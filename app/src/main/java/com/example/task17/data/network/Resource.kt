package com.example.task17.data.network

sealed class Resource<out T> {
    data class Success<out T>(val data: T, val errorMessage: String? = null) : Resource<T>()
    data object Loading : Resource<Nothing>()
    data class Error(
        val errorMessage: String?
    ) : Resource<Nothing>()
}