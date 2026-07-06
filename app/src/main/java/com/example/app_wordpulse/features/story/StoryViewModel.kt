package com.example.app_wordpulse.features.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_wordpulse.data.model.Story
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StoryViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance("https://app-anh-van-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference("stories")

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadStories() {
        Log.d("StoryViewModel", "Loading stories from Realtime Database")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = database.get().await()
                val storyList = mutableListOf<Story>()
                for (child in snapshot.children) {
                    val story = child.getValue(Story::class.java)
                    story?.let { storyList.add(it) }
                }
                Log.d("StoryViewModel", "Successfully loaded ${storyList.size} stories from RTDB")
                _stories.value = storyList
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error loading stories from RTDB", e)
                _errorMessage.value = "Lỗi tải truyện: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
