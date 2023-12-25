package com.example.task17.presentation.screens.fragments.registerfragment

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.task17.core.base.BaseFragment
import com.example.task17.core.helper.Listeners
import com.example.task17.core.helper.Observers
import com.example.task17.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate),
    Listeners, Observers {

    private val registrationResultKey = "registrationResult"
    private val viewModel: RegisterViewModel by viewModels()

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

                setFragmentResult(registrationResultKey, Bundle().apply {
                    putString("email", email)
                    putString("password", password)
                })
                handleSuccessState()
            }
        }

        buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun observers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collect { viewState ->
                    binding.progressBar.isVisible = viewState.isLoading
                    if (viewState.isError) {
                        Snackbar.make(
                            binding.root,
                            viewState.error?.message.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
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
                Snackbar.make(binding.root, "Email is empty!", Snackbar.LENGTH_LONG).show()
            }

            !isValidEmail(email) -> {
                Snackbar.make(binding.root, "Email is not correct!", Snackbar.LENGTH_LONG).show()
            }

            password.isEmpty() -> {
                Snackbar.make(binding.root, "Password is empty!", Snackbar.LENGTH_LONG).show()
            }

            password != repeatPassword -> {
                Snackbar.make(binding.root, "Passwords do not match!", Snackbar.LENGTH_LONG).show()
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
