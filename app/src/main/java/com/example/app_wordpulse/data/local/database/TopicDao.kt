package com.example.app_wordpulse.data.local.database

import androidx.room.Dao
import androidx.room.Query
import com.example.app_wordpulse.data.model.Topic

@Dao
interface TopicDao {
    // Lấy tất cả topic
    @Query("SELECT * FROM topic")
    suspend fun getAllTopics(): List<Topic>

    // Lấy danh sách topic theo level (A1, A2, B1, B2)
    @Query("SELECT * FROM topic WHERE level = :level")
    suspend fun getTopicsByLevel(level: String): List<Topic>

    // Lấy một topic cụ thể theo id
    @Query("SELECT * FROM topic WHERE id = :id LIMIT 1")
    suspend fun getTopicById(id: Int): Topic?
}