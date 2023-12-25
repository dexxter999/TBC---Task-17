package com.example.task17.data.repository


import com.example.task17.data.network.AuthService
import com.example.task17.data.network.Resource
import com.example.task17.data.network.model.request.LoginAndRegisterRequest
import com.example.task17.domain.AuthRepository
import com.example.task17.domain.LoginResponse
import com.example.task17.domain.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authService: AuthService) :
    AuthRepository {

    override suspend fun login(requestBody: LoginAndRegisterRequest): Flow<Resource<LoginResponse>> {
        return flow {
            emit(Resource.Loading)
            try {
                val loginResponse = authService.login(requestBody)
                if (loginResponse.isSuccessful) {
                    emit(Resource.Success(loginResponse.body()!!.toLogin()))
                } else {
                    emit(Resource.Error(loginResponse.errorBody()?.string()))
                }
            } catch (e: IOException) {
                emit(Resource.Error("Network Error"))
            } catch (e: HttpException) {
                emit(Resource.Error("HTTP Error: ${e.code()}"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }

        }.flowOn(Dispatchers.IO)
    }


    override suspend fun register(requestBody: LoginAndRegisterRequest): Flow<Resource<RegisterResponse>> {
        return flow {
            emit(Resource.Loading)
            try {
                val registerResponse = authService.register(requestBody)
                if (registerResponse.isSuccessful) {
                    emit(Resource.Success(registerResponse.body()!!.toRegister()))
                } else {
                    emit(Resource.Error(registerResponse.errorBody()?.string()))
                }
            } catch (e: IOException) {
                emit(Resource.Error("Network Error"))
            } catch (e: HttpException) {
                emit(Resource.Error("HTTP Error: ${e.code()}"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }.flowOn(Dispatchers.IO)
    }

}
