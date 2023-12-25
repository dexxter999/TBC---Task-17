package com.example.task17.presentation.screens.fragments.loginfragment

import android.util.Log.d
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task17.data.network.Resource
import com.example.task17.data.network.model.request.LoginAndRegisterRequest
import com.example.task17.data.repository.DataStoreRepository
import com.example.task17.domain.AuthRepository
import com.example.task17.domain.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginViewState())
    val loginState get() = _loginState

    val navigationEventChannel = Channel<NavigationEvent>(Channel.UNLIMITED)

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> {
                if (event.email.isEmpty() || event.password.isEmpty() || !isValidEmail(event.email)) {
                    _loginState.update {
                        it.copy(
                            error = IllegalArgumentException("Invalid Credentials"),
                            isLoading = false,
                            isError = true
                        )
                    }
                } else {
                    login(event.email, event.password)
                }
            }

            is LoginEvent.ChangeCheckBoxValue -> {
                _loginState.update {
                    it.copy(
                        isChecked = event.checked
                    )
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            val savedEmail = dataStoreRepository.getSavedEmail()
            val savedToken = dataStoreRepository.getSavedToken()
            d("somethingHere", savedEmail)
            if (savedEmail.isNotBlank()) {
                _loginState.update {
                    it.copy(
                        login = LoginResponse(savedToken),
                        isLoading = false,
                        isChecked = true,
                        sessionSaved = true
                    )
                }
                navigationEventChannel.send(NavigationEvent.NavigateToHomeAfterLogIn)
            }
        }
    }

    private fun login(email: String, password: String) {
        val loginRequest = LoginAndRegisterRequest(email, password)
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true) }

            repository.login(loginRequest).collectLatest { result ->
                _loginState.update { it.copy(login = it.login, isLoading = false) }

                if (result is Resource.Success) {
                    if (_loginState.value.isChecked) {

                        _loginState.update {
                            it.copy(
                                login = result.data,
                                sessionSaved = true
                            )
                        }
                        dataStoreRepository.saveSession(email, result.data.token)
                    }

                    _loginState.update {
                        it.copy(
                            login = result.data,
                            isLoading = false,
                            isError = false
                        )
                    }

                    navigationEventChannel.send(NavigationEvent.NavigateToHomeAfterLogIn)
                } else if (result is Resource.Error) {
                    _loginState.update {
                        it.copy(
                            isLoading = false,
                            isError = it.isError
                        )
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    data class LoginViewState(
        val login: LoginResponse? = null,
        val error: Exception? = null,
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        var isChecked: Boolean = false,
        val sessionSaved: Boolean = false
    )

    sealed interface LoginEvent {
        data class Login(val email: String, val password: String) : LoginEvent
        data class ChangeCheckBoxValue(val checked: Boolean) : LoginEvent
    }

    sealed class NavigationEvent {
        data object NavigateToHomeAfterLogIn : NavigationEvent()
        data object NavigateToRegister : NavigationEvent()
    }
}