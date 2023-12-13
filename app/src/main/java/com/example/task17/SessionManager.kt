package com.example.task17

import android.content.Context
import android.content.SharedPreferences


class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val PREF_NAME = "SessionPref"
        private const val KEY_EMAIL = "email"
        private const val KEY_TOKEN = "token"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_REMEMBER_ME = "rememberMe"
    }

    fun saveSession(email: String, token: String, rememberMe: Boolean) {
        editor.apply {
            putString(KEY_EMAIL, email)
            putString(KEY_TOKEN, token)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_REMEMBER_ME, rememberMe)
            apply()
        }
    }

    fun checkLogin(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()
    }
}
