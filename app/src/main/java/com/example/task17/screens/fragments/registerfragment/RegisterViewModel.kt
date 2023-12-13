package com.example.task17.screens.fragments.registerfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task17.network.Resource
import com.example.task17.network.RetrofitInstance
import com.example.task17.network.model.LoginAndRegisterRequest
import com.example.task17.network.model.RegisterResponse
import com.example.task17.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository = AuthRepository(RetrofitInstance.api)) :
    ViewModel() {
    private val _registerState = MutableSharedFlow<Resource<RegisterResponse>>()
    val registerState = _registerState.asSharedFlow()

    fun register(email: String, password: String) {
        val registerRequest = LoginAndRegisterRequest(email = email, password = password)
        viewModelScope.launch {
            repository.register(registerRequest).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _registerState.emit(result)
                    }
                    is Resource.Error -> {
                        _registerState.emit(Resource.Error(result.isNetworkError, result.errorMessage))
                    }
                    is Resource.Loading -> {
                        _registerState.emit(result)
                    }
                }
            }
        }

    }
}