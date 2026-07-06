package com.example.app_wordpulse.features.exercise

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_wordpulse.data.model.DictationLesson
import com.example.app_wordpulse.data.model.TranscriptLine
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DictationViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance("https://app-anh-van-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference("dictation_lessons")

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
                val snapshot = database.child(lessonId).get().await()
                val lesson = snapshot.getValue(DictationLesson::class.java)
                if (lesson != null) {
                    val finalLesson = lesson.copy(
                        id = if (lesson.id.isEmpty()) snapshot.key ?: "" else lesson.id,
                        videoId = if (lesson.videoId.isEmpty()) extractVideoId(lesson.videoUrl) else lesson.videoId
                    )
                    _lessonData.value = finalLesson
                }
            } catch (e: Exception) {
                Log.e("DictationViewModel", "Error loading lesson", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun extractVideoId(url: String): String {
        // Đơn giản hóa việc lấy ID từ link youtube
        return when {
            url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("be/") -> url.substringAfter("be/")
            else -> url
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
