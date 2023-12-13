package com.example.task17.screens

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.task17.R
import com.example.task17.SessionManager
import com.example.task17.screens.fragments.homefragment.HomeFragmentDirections


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val sharedPref = getSharedPreferences("SessionPref", Context.MODE_PRIVATE)

        val token = sharedPref.getString("token", null)

        if (token != null) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
        } else {
            findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
        }
    }
}
