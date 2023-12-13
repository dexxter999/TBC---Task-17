package com.example.task17.screens.fragments.loginfragment

import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.task17.SessionManager
import com.example.task17.base.BaseFragment
import com.example.task17.databinding.FragmentLoginBinding
import com.example.task17.factory.createViewModelFactory
import com.example.task17.interfaces.Listeners
import com.example.task17.interfaces.Observers
import com.example.task17.network.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate), Listeners,
    Observers {

    private val viewModel: LoginViewModel by viewModels {
        createViewModelFactory {
            LoginViewModel(
                sessionManager = sessionManager
            )
        }
    }
    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    override fun init() {
        getInfo()
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

            if (validateInput(email, password)) {
                viewModel.login(email, password)
            }
        }
    }

    override fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            showLoading(false)
                            if (binding.cbRememberMe.isChecked) {
                                sessionManager.saveSession(
                                    email = binding.etEmail.text.toString(),
                                    token = it.data.token,
                                    rememberMe = true
                                )
                            }
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
    }

    private fun navigateToHome() {
        val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun getInfo() = with(binding){
        setFragmentResultListener("registrationResult") { _, result ->
            val email = result.getString("email")
            val password = result.getString("password")

            etEmail.setText(email)
            etPassword.setText(password)
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
