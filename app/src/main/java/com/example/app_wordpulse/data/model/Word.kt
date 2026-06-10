package com.example.app_wordpulse.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Word(
    @PrimaryKey val id: Int,
    val term: String,
    val definition: String,
    val example: String? = null
)
