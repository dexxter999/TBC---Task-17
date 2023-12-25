package com.example.task17.presentation.screens.fragments.registerfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task17.data.network.Resource
import com.example.task17.data.network.model.request.LoginAndRegisterRequest
import com.example.task17.domain.AuthRepository
import com.example.task17.domain.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: AuthRepository) :
    ViewModel() {
    private val _registerState = MutableStateFlow(RegisterViewState())
    val registerState get() = _registerState.asSharedFlow()

    private val _intentChannel = Channel<RegisterEvent>(Channel.UNLIMITED)

    init {
        viewModelScope.launch {
            _intentChannel.consumeAsFlow().collect { event ->
                when (event) {
                    is RegisterEvent.Register -> register(event.email, event.password)
                }
            }
        }
    }

    private fun register(email: String, password: String) {
        val registerRequest = LoginAndRegisterRequest(email, password)
        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true) }

            repository.register(registerRequest).collect { x ->
                _registerState.update { it.copy(register = x, isLoading = false) }
            }
        }
    }
}


data class RegisterViewState(
    val register: Resource<RegisterResponse> = Resource.Loading,
    val error: Exception? = null,
    val isError: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface RegisterEvent {
    data class Register(val email: String, val password: String, val repeatPassword: String) :
        RegisterEvent
}