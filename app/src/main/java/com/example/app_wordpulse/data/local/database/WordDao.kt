package com.example.app_wordpulse.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.app_wordpulse.data.model.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM Words ORDER BY id")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT DISTINCT topic FROM Words WHERE topic IS NOT NULL AND TRIM(topic) != '' ORDER BY topic")
    fun getAllTopics(): Flow<List<String>>

    @Query("SELECT DISTINCT level FROM Words WHERE level IS NOT NULL AND TRIM(level) != '' ORDER BY level")
    fun getAllLevels(): Flow<List<String>>

    @Query("SELECT * FROM Words WHERE topic = :topic ORDER BY id")
    fun getWordsByTopic(topic: String): Flow<List<Word>>

    @Query("SELECT * FROM Words WHERE level = :level ORDER BY id")
    fun getWordsByLevel(level: String): Flow<List<Word>>

    @Insert
    suspend fun insertWord(word: Word)
}
