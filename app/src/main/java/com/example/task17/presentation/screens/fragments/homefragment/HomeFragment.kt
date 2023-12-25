package com.example.task17.presentation.screens.fragments.homefragment

import androidx.activity.addCallback
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.task17.core.base.BaseFragment
import com.example.task17.core.helper.Listeners
import com.example.task17.data.repository.DataStoreRepository
import com.example.task17.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), Listeners {

    @Inject
    lateinit var dataStore: DataStoreRepository

    override fun init() {
        collectData()
        listeners()
    }

    private fun collectData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dataStore.getEmail.collect { email ->
                    binding.tvEmail.text = email
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