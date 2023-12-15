package com.example.task17.screens.fragments.loginfragment

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.task17.R
import com.example.task17.network.Resource
import com.example.task17.network.RetrofitInstance
import com.example.task17.network.model.LoginAndRegisterRequest
import com.example.task17.network.model.LoginResponse
import com.example.task17.repository.AuthRepository
import com.example.task17.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository(RetrofitInstance.api),
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _loginState = MutableSharedFlow<Resource<LoginResponse>>()
    val loginState = _loginState.asSharedFlow()

    private val _validationResult = MutableStateFlow<LoginValidationResult>(LoginValidationResult.Success)
    val validationResult = _validationResult.asStateFlow()

    private val _sessionExists = MutableStateFlow(false)
    val sessionExists: StateFlow<Boolean> = _sessionExists.asStateFlow()

    private val _rememberMeChecked = MutableStateFlow(false)
    val rememberMeChecked: StateFlow<Boolean> = _rememberMeChecked.asStateFlow()

    init {
        checkSession()
    }

    fun setRememberMe(checked: Boolean) {
        _rememberMeChecked.value = checked
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            combine(dataStoreRepository.getEmail, rememberMeChecked) { email, rememberMeChecked ->
                email.isNotEmpty() && rememberMeChecked
            }.collectLatest { sessionExists ->
                _sessionExists.value = sessionExists
            }
        }
    }

    fun login(email: String, password: String) {
        val loginRequest = LoginAndRegisterRequest(email = email, password = password)
        viewModelScope.launch {
            repository.login(loginRequest).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        if  (_rememberMeChecked.value) {
                            dataStoreRepository.saveSession(email, result.data.token, true)
                        }
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

    fun validateInput(email: String, password: String): Boolean {
        _validationResult.value = when {
            email.isEmpty() -> LoginValidationResult.Error("Email is empty!")
            password.isEmpty() -> LoginValidationResult.Error("Password is empty!")
            !isValidEmail(email) -> LoginValidationResult.Error("Email is not correct!")
            else -> LoginValidationResult.Success
        }
        return _validationResult.value is LoginValidationResult.Success
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
