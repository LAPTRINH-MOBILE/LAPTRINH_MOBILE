package com.example.app_wordpulse.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.app_wordpulse.utils.Constants

class UserPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    fun saveLoginStatus(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
    }
}
