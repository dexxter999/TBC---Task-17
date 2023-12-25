package com.example.task17.presentation.screens.fragments.loginfragment

import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.task17.R
import com.example.task17.core.base.BaseFragment
import com.example.task17.core.helper.Listeners
import com.example.task17.core.helper.Observers
import com.example.task17.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate), Listeners,
    Observers {


    private val viewModel: LoginViewModel by viewModels()

    override fun init() {
        listeners()
        observers()
    }

    override fun listeners() = with(binding) {
        tvNotRegistered.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        buttonLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            viewModel.onEvent(LoginViewModel.LoginEvent.Login(email, password))
        }

        cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onEvent(LoginViewModel.LoginEvent.ChangeCheckBoxValue(isChecked))
        }


    }

    override fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { viewState ->
                    binding.progressBar.isVisible = viewState.isLoading

                    if (viewState.isError) {
                        Snackbar.make(
                            binding.root, viewState.error?.message.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEventChannel.consumeAsFlow().collect { navigation ->
                    handleNavigationEvent(navigation)
                }
            }
        }

    }

    private fun handleNavigationEvent(navigationEvent: LoginViewModel.NavigationEvent) {
        when (navigationEvent) {
            is LoginViewModel.NavigationEvent.NavigateToRegister -> {
                val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                findNavController().navigate(action)
            }

            is LoginViewModel.NavigationEvent.NavigateToHomeAfterLogIn -> {
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                findNavController().navigate(action)
            }
        }
    }
}