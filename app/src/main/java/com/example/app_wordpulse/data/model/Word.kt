package com.example.app_wordpulse.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val term: String? = null,
    val pronunciation: String? = null,
    @ColumnInfo(name = "word_type")
    val wordType: String? = null,
    val definition: String? = null,
    @ColumnInfo(name = "topic")
    val topicName: String? = null,
    val level: String? = null,
    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null
)
