package com.example.app_wordpulse.features.auth

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    fun login(username: String, password: String): Boolean {
        // Logic login
        return username == "admin" && password == "123"
    }
}
