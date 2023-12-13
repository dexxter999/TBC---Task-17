package com.example.task17.repository

import com.example.task17.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseRepository {
    suspend fun <T> apiCall(apiCall: suspend () -> Response<T>): Flow<Resource<T>> = flow {
        try {
            val response = apiCall.invoke()

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(false, "Code: ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> emit(Resource.Error(true, "Check your internet connection $e"))
                is HttpException -> emit(Resource.Error(false, "Error $e"))
                else -> emit(Resource.Error(true, "Something's went wrong $e"))
            }
        }
    }.flowOn(Dispatchers.IO)
}