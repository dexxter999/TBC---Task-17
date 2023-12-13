package com.example.task17.screens.fragments.registerfragment

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.task17.base.BaseFragment
import com.example.task17.databinding.FragmentRegisterBinding
import com.example.task17.factory.createViewModelFactory
import com.example.task17.interfaces.Listeners
import com.example.task17.interfaces.Observers
import com.example.task17.network.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate),
    Listeners, Observers {

    private val registrationResultKey = "registrationResult"
    private val viewModel: RegisterViewModel by viewModels { createViewModelFactory { RegisterViewModel() } }

    override fun init() {
        observers()
        listeners()
    }

    override fun listeners() = with(binding) {
        buttonRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val repeatPassword = etRepeatPassword.text.toString()

            if (validateInput(email, password, repeatPassword)) {
                viewModel.register(email, password)
                setFragmentResult(registrationResultKey, Bundle().apply {
                    putString("email", email)
                    putString("password", password)
                })
            }
        }

        buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collectLatest {
                    when (it) {
                        is Resource.Success -> handleSuccessState()
                        is Resource.Error -> {
                            showLoading(false)
                            showToast(it.errorMessage ?: "")
                        }

                        is Resource.Loading -> showLoading(true)
                    }
                }
            }
        }
    }

    private fun handleSuccessState() {
        showLoading(false).also {
            navigateToLogin()
            showToast("Successfully registered!")
            clearAll()
        }
    }

    private fun navigateToLogin() {
        findNavController().popBackStack()
    }

    private fun validateInput(email: String, password: String, repeatPassword: String): Boolean {
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

            password != repeatPassword -> {
                showToast("Passwords do not match!")
            }

            else -> isValid = true
        }
        return isValid
    }

    private fun clearAll() = with(binding) {
        etEmail.text!!.clear()
        etPassword.text!!.clear()
        etRepeatPassword.text!!.clear()
    }

    private fun showToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
