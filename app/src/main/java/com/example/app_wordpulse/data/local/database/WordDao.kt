package com.example.app_wordpulse.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.app_wordpulse.data.model.Word

@Dao
interface WordDao {
    @Query("SELECT * FROM Word")
    fun getAllWords(): List<Word>

    @Insert
    fun insertWord(word: Word)
}
