package com.example.app_wordpulse.features.grammar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app_wordpulse.data.local.database.GrammarDbHelper
import com.example.app_wordpulse.data.model.GrammarExercise

class GrammarViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = GrammarDbHelper(application)
    private var exercises: List<GrammarExercise> = emptyList()
    private var currentIndex = 0
    private var correctCount = 0
    private var wrongCount = 0
    private var hasLoaded = false
    private var hasCheckedCurrentExercise = false

    val totalCount: Int
        get() = exercises.size

    val correctAnswerCount: Int
        get() = correctCount

    val wrongAnswerCount: Int
        get() = wrongCount

    val isLastExercise: Boolean
        get() = exercises.isNotEmpty() && currentIndex == exercises.lastIndex

    private val _currentExercise = MutableLiveData<GrammarExercise?>()
    val currentExercise: LiveData<GrammarExercise?> = _currentExercise

    private val _progressText = MutableLiveData("Câu: 0/0")
    val progressText: LiveData<String> = _progressText

    private val _progressPercent = MutableLiveData(0)
    val progressPercent: LiveData<Int> = _progressPercent

    private val _scoreText = MutableLiveData("Đúng: 0 | Sai: 0")
    val scoreText: LiveData<String> = _scoreText

    private val _isSessionFinished = MutableLiveData(false)
    val isSessionFinished: LiveData<Boolean> = _isSessionFinished

    fun loadExercises(topicId: Int, level: String?) {
        if (hasLoaded) return

        // Grammar dùng raw SQLite trong dataEl.db, tách biệt khỏi Room entity Word.
        val selectedLevel = level?.trim()?.takeIf { it.isNotEmpty() }
        val loadedExercises = when {
            topicId > 0 && selectedLevel != null -> dbHelper.getExercisesByTopicAndLevel(topicId, selectedLevel)
            topicId > 0 -> dbHelper.getExercisesByTopic(topicId)
            selectedLevel != null -> dbHelper.getExercisesByLevel(selectedLevel)
            else -> dbHelper.getAllExercises().shuffled()
        }

        exercises = loadedExercises.take(SESSION_EXERCISE_COUNT)
        currentIndex = 0
        correctCount = 0
        wrongCount = 0
        hasLoaded = true
        hasCheckedCurrentExercise = false
        _isSessionFinished.value = false
        publishCurrentExercise()
        publishScore()
    }

    fun checkGrammar(userInput: String, correctAnswer: String): Boolean {
        // So sánh đáp án theo yêu cầu: bỏ khoảng trắng hai đầu và không phân biệt hoa/thường.
        val isCorrect = userInput.trim().equals(correctAnswer.trim(), ignoreCase = true)

        if (!hasCheckedCurrentExercise) {
            if (isCorrect) {
                correctCount++
            } else {
                wrongCount++
            }
            hasCheckedCurrentExercise = true
            publishScore()
        }

        return isCorrect
    }

    fun nextExercise(): Boolean {
        if (currentIndex < exercises.lastIndex) {
            currentIndex++
            hasCheckedCurrentExercise = false
            publishCurrentExercise()
            return true
        }

        _isSessionFinished.value = true
        _currentExercise.value = null
        _progressText.value = "Hoàn thành"
        _progressPercent.value = 100
        return false
    }

    private fun publishCurrentExercise() {
        val total = exercises.size
        _currentExercise.value = exercises.getOrNull(currentIndex)
        _progressText.value = if (total > 0) {
            "Câu: ${currentIndex + 1}/$total"
        } else {
            "Câu: 0/0"
        }

        _progressPercent.value = if (total > 0) {
            ((currentIndex + 1) * 100) / total
        } else {
            0
        }
    }

    private fun publishScore() {
        _scoreText.value = "Đúng: $correctCount | Sai: $wrongCount"
    }

    companion object {
        private const val SESSION_EXERCISE_COUNT = 15
    }
}
