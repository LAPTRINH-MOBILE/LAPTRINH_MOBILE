package com.example.app_wordpulse.features.exercise

import android.util.Log
import java.util.Locale
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_wordpulse.data.model.DictationLesson
import com.example.app_wordpulse.data.model.LessonCategory
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LessonListViewModel : ViewModel() {
    // Kết nối tới root của database để kiểm tra cấu trúc
    private val databaseRoot = FirebaseDatabase.getInstance("https://app-anh-van-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference()

    private val _categories = MutableLiveData<List<LessonCategory>>()
    val categories: LiveData<List<LessonCategory>> = _categories

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadCategories() {
        Log.d("LessonListViewModel", "Loading categories from Realtime Database...")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val categoryList = mutableListOf<LessonCategory>()
                
                // 1. Lấy dữ liệu từ node "dictation_lessons"
                val snapshot = databaseRoot.child("dictation_lessons").get().await()
                
                if (snapshot.exists()) {
                    Log.d("LessonListViewModel", "Found dictation_lessons node")
                    val allLessons = mutableListOf<DictationLesson>()
                    
                    for (lessonChild in snapshot.children) {
                        val lesson = lessonChild.getValue(DictationLesson::class.java)
                        if (lesson != null) {
                            val videoLink = lesson.videoUrl.ifEmpty {
                                lessonChild.child("videoUrl").getValue(String::class.java) ?:
                                lessonChild.child("url").getValue(String::class.java) ?:
                                lessonChild.child("video_url").getValue(String::class.java) ?: ""
                            }
                            
                            val thumbLink = lesson.thumbnailUrl.ifEmpty {
                                lessonChild.child("thumbnailUrl").getValue(String::class.java) ?:
                                lessonChild.child("thumbnail_url").getValue(String::class.java) ?:
                                lessonChild.child("image").getValue(String::class.java) ?:
                                lessonChild.child("imageUrl").getValue(String::class.java) ?:
                                lessonChild.child("thumbnail").getValue(String::class.java) ?: ""
                            }

                            // TÍNH TOÁN DURATION TỔNG: 
                            // 1. Ưu tiên lấy từ field duration/time/length trong DB
                            // 2. Nếu không có, lấy endTime của câu transcript cuối cùng
                            var durationValue = lesson.duration
                            if (durationValue.isEmpty() || durationValue == "00:00") {
                                val dbDuration = lessonChild.child("duration").value ?: 
                                                 lessonChild.child("time").value ?: 
                                                 lessonChild.child("length").value
                                
                                when (dbDuration) {
                                    is String -> durationValue = dbDuration
                                    is Number -> durationValue = formatSeconds(dbDuration.toInt())
                                }

                                // Nếu vẫn chưa có, lấy từ transcript cuối
                                if (durationValue.isEmpty() || durationValue == "00:00") {
                                    if (lesson.transcripts.isNotEmpty()) {
                                        val lastEndTime = lesson.transcripts.last().endTime
                                        durationValue = formatSeconds(lastEndTime.toInt())
                                    }
                                }
                            }

                            val finalLesson = lesson.copy(
                                id = if (lesson.id.isEmpty()) lessonChild.key ?: "" else lesson.id,
                                videoUrl = videoLink,
                                thumbnailUrl = thumbLink,
                                duration = if (durationValue.isEmpty()) "00:00" else durationValue
                            )
                            allLessons.add(finalLesson)
                        }
                    }
                    
                    if (allLessons.isNotEmpty()) {
                        // Phân loại bài học vào 5 chủ đề dựa trên tiêu đề hoặc dữ liệu
                        val businessKeywords = listOf("Business", "Job", "Interview", "Investopedia", "Inflation", "Advertising", "Cover Letter", "Bitcoin", "Grammarly", "Raising")
                        val businessLessons = allLessons.filter { lesson -> 
                            businessKeywords.any { keyword -> lesson.title.contains(keyword, ignoreCase = true) }
                        }
                        
                        val filmKeywords = listOf("Film", "Movie", "Scene", "Trailer", "Teaser", "Theater", "Minions", "Moana", "Featurette", "Angry Birds")
                        val filmLessons = allLessons.filter { lesson ->
                            filmKeywords.any { keyword -> lesson.title.contains(keyword, ignoreCase = true) }
                        }
                        
                        val musicKeywords = listOf("Music", "Song", "Audio", "Official Video", "Official Clip", "Alan Walker", "Charlie Puth", "Maroon 5", "Taylor Swift", "Wiz Khalifa", "Passenger", "Shayne Ward", "Faded", "Memories", "Style", "Blank Space", "Until You", "Let Her Go")
                        val musicLessons = allLessons.filter { lesson ->
                            musicKeywords.any { keyword -> lesson.title.contains(keyword, ignoreCase = true) } && 
                            lesson !in businessLessons && lesson !in filmLessons
                        }
                        val dailyKeywords = listOf("Daily", "Nas", "Language", "Grew Up", "Married", "Vegan", "Die", "World", "Job", "He Knows", "She Has")
                        val dailyLessons = allLessons.filter { lesson ->
                            dailyKeywords.any { keyword -> lesson.title.contains(keyword, ignoreCase = true) } &&
                            lesson !in businessLessons && lesson !in filmLessons && lesson !in musicLessons
                        }

                        // Toàn bộ các clip còn lại là của Short clip
                        val shortClips = allLessons.filter { 
                            it !in businessLessons && it !in filmLessons && it !in musicLessons && it !in dailyLessons 
                        }
                        
                        // Thêm các chủ đề vào danh sách hiển thị
                        if (businessLessons.isNotEmpty()) categoryList.add(LessonCategory("cat_business", "Business English", businessLessons.size, businessLessons))
                        if (filmLessons.isNotEmpty()) categoryList.add(LessonCategory("cat_film", "Film", filmLessons.size, filmLessons))
                        if (musicLessons.isNotEmpty()) categoryList.add(LessonCategory("cat_music", "Music", musicLessons.size, musicLessons))
                        if (dailyLessons.isNotEmpty()) categoryList.add(LessonCategory("cat_daily", "NAs Daily", dailyLessons.size, dailyLessons))
                        if (shortClips.isNotEmpty()) categoryList.add(LessonCategory("cat_short", "Short clip", shortClips.size, shortClips))
                    }
                } else {
                    Log.d("LessonListViewModel", "Node 'dictation_lessons' not found")
                    _errorMessage.value = "Không tìm thấy dữ liệu bài học trên máy chủ."
                }

                // Cập nhật dữ liệu thật lên giao diện
                Log.d("LessonListViewModel", "Successfully loaded ${categoryList.size} categories from RTDB")
                _categories.value = categoryList
                
            } catch (e: Exception) {
                Log.e("LessonListViewModel", "Error loading categories", e)
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
                _categories.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatSeconds(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}
