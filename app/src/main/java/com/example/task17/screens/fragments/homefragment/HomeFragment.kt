package com.example.task17.screens.fragments.homefragment

import androidx.activity.addCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.task17.base.BaseFragment
import com.example.task17.databinding.FragmentHomeBinding
import com.example.task17.interfaces.Listeners
import com.example.task17.repository.DataStoreRepository
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), Listeners {

    private val dataStore: DataStoreRepository by lazy { DataStoreRepository(requireContext()) }

    override fun init() {
        collectData()
        listeners()
    }

    private fun collectData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dataStore.getEmail.collect {
                    binding.tvEmail.text = it
                }
            }
        }
    }

    override fun listeners() {
        binding.buttonLogOut.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                dataStore.clearSession()
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }
}