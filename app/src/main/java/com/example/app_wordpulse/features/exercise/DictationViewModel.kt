package com.example.app_wordpulse.features.exercise

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_wordpulse.data.model.DictationLesson
import com.example.app_wordpulse.data.repository.DictationRepository
import kotlinx.coroutines.launch

class DictationViewModel : ViewModel() {

    private val repository = DictationRepository()

    private val _lessonData = MutableLiveData<DictationLesson>()
    val lessonData: LiveData<DictationLesson> = _lessonData

    private val _currentSentenceIndex = MutableLiveData<Int>(0)
    val currentSentenceIndex: LiveData<Int> = _currentSentenceIndex

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lesson = repository.getLessonById(lessonId)
                if (lesson != null) {
                    _lessonData.value = lesson!!
                } else {
                    Log.e("DictationViewModel", "Lesson not found for ID: $lessonId")
                }
            } catch (e: Exception) {
                Log.e("DictationViewModel", "Error loading lesson", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun moveToNextSentence() {
        val total = _lessonData.value?.transcripts?.size ?: 0
        val current = _currentSentenceIndex.value ?: 0
        if (current < total - 1) {
            _currentSentenceIndex.value = current + 1
        }
    }

    fun setCurrentSentenceIndex(index: Int) {
        _currentSentenceIndex.value = index
    }

    fun getMaskedWordList(text: String): List<String> {
        return text.split(" ").map { word ->
            word.map { if (it.isLetterOrDigit()) '*' else it }.joinToString("")
        }
    }

    fun isAnswerCorrect(userInput: String, correctAnswer: String): Boolean {
        val regex = Regex("[^a-zA-Z0-9]")
        val cleanUser = userInput.replace(regex, "").lowercase().trim()
        val cleanCorrect = correctAnswer.replace(regex, "").lowercase().trim()
        return cleanUser == cleanCorrect
    }
}
