package com.example.app_wordpulse.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val term: String,
    val pronunciation: String,
    @ColumnInfo(name = "word_type")
    val wordType: String,
    val definition: String,
    @ColumnInfo(name = "topic")
    val topicName: String,
    val level: String
)
