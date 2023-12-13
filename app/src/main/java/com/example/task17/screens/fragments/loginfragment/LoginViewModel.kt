package com.example.task17.screens.fragments.loginfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task17.SessionManager
import com.example.task17.network.Resource
import com.example.task17.network.RetrofitInstance
import com.example.task17.network.model.LoginAndRegisterRequest
import com.example.task17.network.model.LoginResponse
import com.example.task17.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository(RetrofitInstance.api),
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _loginState = MutableSharedFlow<Resource<LoginResponse>>()
    val loginState = _loginState.asSharedFlow()

    fun login(email: String, password: String) {
        val loginRequest = LoginAndRegisterRequest(email = email, password = password)
        viewModelScope.launch {
            repository.login(loginRequest).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        sessionManager.saveSession(email, result.data.token, true)
                        _loginState.emit(result)
                    }
                    is Resource.Error -> {
                        _loginState.emit(Resource.Error(result.isNetworkError, result.errorMessage))
                    }
                    is Resource.Loading -> {
                        _loginState.emit(result)
                    }
                }
            }
        }
    }
}