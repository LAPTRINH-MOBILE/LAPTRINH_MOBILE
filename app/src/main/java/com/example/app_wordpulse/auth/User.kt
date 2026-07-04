package com.example.app_wordpulse.auth

data class User(
    val id: String,
    val username: String,
    val email: String,
    val level: Int = 1,
    val totalXp: Int = 0,
    val currentStreak: Int = 0,
    val createdAt: String
)
