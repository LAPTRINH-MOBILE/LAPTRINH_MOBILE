package com.example.app_wordpulse.data.local.database

import androidx.room.Dao
import androidx.room.Query
import com.example.app_wordpulse.data.model.Story

@Dao
interface StoryDao {
    @Query("SELECT * FROM story")
    fun getAllStories(): List<Story>

    @Query("SELECT * FROM story WHERE topic_id = :topicId")
    fun getStoriesByTopic(topicId: Int): List<Story>

    @Query("SELECT * FROM story WHERE id = :id LIMIT 1")
    suspend fun getStoryById(id: Int): Story?
}
