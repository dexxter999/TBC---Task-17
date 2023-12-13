package com.example.task17.screens.fragments.homefragment

import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.example.task17.SessionManager
import com.example.task17.base.BaseFragment
import com.example.task17.databinding.FragmentHomeBinding
import com.example.task17.interfaces.Listeners

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), Listeners {

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    override fun init() {
        if (!sessionManager.checkLogin()) {
            pop()
        } else {
            val userEmail = sessionManager.getUserEmail()
            binding.tvEmail.text = userEmail
        }
        listeners()

    }

    override fun listeners() {
        binding.buttonLogOut.setOnClickListener {
            logout()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    private fun pop() = findNavController().popBackStack()

    private fun logout() {
        sessionManager.logoutUser()
        pop()
    }


}