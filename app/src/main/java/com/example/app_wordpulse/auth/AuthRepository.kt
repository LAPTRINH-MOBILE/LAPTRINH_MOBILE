package com.example.app_wordpulse.auth

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class AuthRepository(private val context: Context) {

    private val dbHelper = AuthDbHelper(context)
    private val prefs: SharedPreferences = context.getSharedPreferences("lingua_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_CURRENT_USER = "current_user_json"
    }

    suspend fun register(username: String, email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val trimmedUsername = username.trim()
        val trimmedEmail = email.trim().lowercase(Locale.ROOT)

        if (trimmedUsername.length < 2) {
            return@withContext AuthResult.Error("Username must be at least 2 characters.")
        }
        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            return@withContext AuthResult.Error("Please enter a valid email address.")
        }
        if (password.length < 6) {
            return@withContext AuthResult.Error("Password must be at least 6 characters.")
        }
        if (dbHelper.isEmailExists(trimmedEmail)) {
            return@withContext AuthResult.Error("An account with this email already exists.")
        }

        val passwordHash = dbHelper.hashPassword(password)
        val result = dbHelper.insertUser(trimmedUsername, trimmedEmail, passwordHash)

        if (result != -1L) {
            val storedUser = dbHelper.findUserByEmail(trimmedEmail)
            if (storedUser != null) {
                val user = storedUser.toUser()
                saveSession(user)
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Registration failed. Please try again.")
            }
        } else {
            AuthResult.Error("Registration failed. Please try again.")
        }
    }

    suspend fun login(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase(Locale.ROOT)

        if (trimmedEmail.isEmpty()) {
            return@withContext AuthResult.Error("Email is required.")
        }
        if (password.isEmpty()) {
            return@withContext AuthResult.Error("Password is required.")
        }

        val storedUser = dbHelper.findUserByEmail(trimmedEmail)
        if (storedUser != null) {
            val passwordHash = dbHelper.hashPassword(password)
            if (storedUser.passwordHash == passwordHash) {
                val user = storedUser.toUser()
                saveSession(user)
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Incorrect email or password.")
            }
        } else {
            AuthResult.Error("Incorrect email or password.")
        }
    }

    fun logout() {
        prefs.edit().remove(KEY_CURRENT_USER).apply()
    }

    fun getCurrentUser(): User? {
        val json = prefs.getString(KEY_CURRENT_USER, null)
        return if (json != null) {
            gson.fromJson(json, User::class.java)
        } else {
            null
        }
    }

    private fun saveSession(user: User) {
        val json = gson.toJson(user)
        prefs.edit().putString(KEY_CURRENT_USER, json).apply()
    }
}
