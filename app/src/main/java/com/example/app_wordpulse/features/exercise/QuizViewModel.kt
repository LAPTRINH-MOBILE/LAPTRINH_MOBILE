package com.example.app_wordpulse.features.exercise

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {
    fun checkAnswer(selected: String, correct: String): Boolean {
        return selected == correct
    }
}
