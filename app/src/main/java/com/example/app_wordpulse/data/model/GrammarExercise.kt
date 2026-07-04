package com.example.app_wordpulse.data.model

data class GrammarExercise(
    val id: Int,
    val topicId: Int,
    val level: String,
    val vietnameseSentence: String,
    val correctAnswer: String,
    val grammarTopic: String,
    val explanationVi: String
)
