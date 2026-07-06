package com.example.app_wordpulse.features.vocabulary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_wordpulse.data.local.database.VocabularyDatabase
import com.example.app_wordpulse.data.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VocabViewModel(application: Application) : AndroidViewModel(application) {
    private val db = VocabularyDatabase.getDatabase(application)
    private val wordDao = db.wordDao()

    private val _topics = MutableStateFlow<List<String>>(emptyList())
    val topics: StateFlow<List<String>> = _topics.asStateFlow()

    private val _levels = MutableStateFlow<List<String>>(emptyList())
    val levels: StateFlow<List<String>> = _levels.asStateFlow()

    private val _words = MutableStateFlow<List<Word>>(emptyList())
    val words: StateFlow<List<Word>> = _words.asStateFlow()

    init {
        // loadTopics() - Removed to avoid loading all topics initially
        loadLevels()
    }

    private fun loadTopics() {
        // No longer used, but kept as empty or removed to ensure loadTopicsByLevel works
    }

    private fun loadLevels() {
        viewModelScope.launch {
            try {
                wordDao.getAllLevels().collectLatest { levels ->
                    _levels.value = levels
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadWordsByTopic(topic: String) {
        viewModelScope.launch {
            try {
                wordDao.getWordsByTopic(topic).collectLatest { words ->
                    _words.value = words
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadWordsByLevel(level: String) {
        viewModelScope.launch {
            try {
                wordDao.getWordsByLevel(level).collectLatest { words ->
                    _words.value = words
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadTopicsByLevel(level: String) {
        viewModelScope.launch {
            try {
                wordDao.getTopicsByLevel(level).collectLatest { topics ->
                    _topics.value = topics
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
