package com.example.task17.screens.fragments.loginfragment

import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.task17.R
import com.example.task17.base.BaseFragment
import com.example.task17.databinding.FragmentLoginBinding
import com.example.task17.factory.createViewModelFactory
import com.example.task17.interfaces.Listeners
import com.example.task17.interfaces.Observers
import com.example.task17.network.Resource
import com.example.task17.repository.DataStoreRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate), Listeners,
    Observers {

    private var isNavigationInProgress = false

    private val viewModel: LoginViewModel by viewModels {
        createViewModelFactory {
            LoginViewModel(
                dataStoreRepository = dataStore
            )
        }
    }

    private val dataStore: DataStoreRepository by lazy { DataStoreRepository(requireContext()) }

    override fun init() {
        listeners()
        observers()
    }

    override fun listeners() = with(binding) {
        tvNotRegistered.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        buttonLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val rememberMeChecked = cbRememberMe.isChecked

            viewModel.setRememberMe(rememberMeChecked)

            if (validateInput(email, password)) {
                viewModel.login(email, password)
            }
            navigateToHome()
        }
    }

    override fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            showLoading(false)
                            navigateToHome()
                        }

                        is Resource.Error -> {
                            showLoading(false)
                            showToast(it.errorMessage!!)
                        }

                        is Resource.Loading -> showLoading(true)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sessionExists.collectLatest { sessionExists ->

                }
            }
        }
    }

    private fun navigateToHome() {
        if (viewModel.sessionExists.value) {
            val action =
                LoginFragmentDirections.actionLoginFragmentToHomeFragment(
                    rememberMeChecked = viewModel.rememberMeChecked.value
                )
            findNavController().navigate(action)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = false
        when {
            email.isEmpty() -> {
                showToast("Email is empty!")
            }

            !isValidEmail(email) -> {
                showToast("Email is not correct!")
            }

            password.isEmpty() -> {
                showToast("Password is empty!")
            }

            else -> isValid = true
        }
        return isValid
    }

    private fun showToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()
}