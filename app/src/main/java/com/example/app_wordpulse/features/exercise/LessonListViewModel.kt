package com.example.app_wordpulse.features.exercise

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_wordpulse.data.model.DictationLesson
import com.example.app_wordpulse.data.model.LessonCategory
import com.example.app_wordpulse.data.repository.DictationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LessonListViewModel : ViewModel() {
    private val repository = DictationRepository()

    private val _categories = MutableLiveData<List<LessonCategory>>()
    val categories: LiveData<List<LessonCategory>> = _categories

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val categoryList = withContext(Dispatchers.IO) {
                    val allLessons = repository.getAllLessons()
                    if (allLessons.isNotEmpty()) {
                        createCategories(allLessons)
                    } else {
                        emptyList()
                    }
                }
                
                if (categoryList.isEmpty()) {
                    _errorMessage.value = "Không tìm thấy dữ liệu bài học."
                } else {
                    _categories.value = categoryList
                }
            } catch (e: Exception) {
                Log.e("LessonListViewModel", "Error loading categories", e)
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun createCategories(allLessons: List<DictationLesson>): List<LessonCategory> {
        val categoryList = mutableListOf<LessonCategory>()
        
        val businessKeywords = listOf("Business", "Job", "Interview", "Investopedia", "Inflation", "Advertising", "Cover Letter", "Bitcoin", "Grammarly", "Raising")
        val filmKeywords = listOf("Film", "Movie", "Scene", "Trailer", "Teaser", "Theater", "Minions", "Moana", "Featurette", "Angry Birds")
        val musicKeywords = listOf("Music", "Song", "Audio", "Official Video", "Official Clip", "Alan Walker", "Charlie Puth", "Maroon 5", "Taylor Swift", "Wiz Khalifa", "Passenger", "Shayne Ward", "Faded", "Memories", "Style", "Blank Space", "Until You", "Let Her Go")
        val dailyKeywords = listOf("Daily", "Nas", "Language", "Grew Up", "Married", "Vegan", "Die", "World", "Job", "He Knows", "She Has")

        val businessLessons = allLessons.filter { lesson -> businessKeywords.any { lesson.title.contains(it, ignoreCase = true) } }
        val filmLessons = allLessons.filter { lesson -> filmKeywords.any { lesson.title.contains(it, ignoreCase = true) } }
        val musicLessons = allLessons.filter { lesson -> musicKeywords.any { lesson.title.contains(it, ignoreCase = true) } && lesson !in businessLessons && lesson !in filmLessons }
        val dailyLessons = allLessons.filter { lesson -> dailyKeywords.any { lesson.title.contains(it, ignoreCase = true) } && lesson !in businessLessons && lesson !in filmLessons && lesson !in musicLessons }
        val shortClips = allLessons.filter { it !in businessLessons && it !in filmLessons && it !in musicLessons && it !in dailyLessons }

        if (businessLessons.isNotEmpty()) categoryList.add(LessonCategory("cat_business", "Business English", businessLessons.size, businessLessons))
        if (filmLessons.isNotEmpty()) categoryList.add(LessonCategory("cat_film", "Film", filmLessons.size, filmLessons))
        if (musicLessons.isNotEmpty()) categoryList.add(LessonCategory("cat_music", "Music", musicLessons.size, musicLessons))
        if (dailyLessons.isNotEmpty()) categoryList.add(LessonCategory("cat_daily", "NAs Daily", dailyLessons.size, dailyLessons))
        if (shortClips.isNotEmpty()) categoryList.add(LessonCategory("cat_short", "Short clip", shortClips.size, shortClips))
        
        return categoryList
    }
}
