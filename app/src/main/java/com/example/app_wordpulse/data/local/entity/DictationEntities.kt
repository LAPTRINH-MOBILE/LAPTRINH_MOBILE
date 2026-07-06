package com.example.app_wordpulse.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_categories")
data class CategoryEntity(
    @PrimaryKey val categoryId: String,
    val name: String
)

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LessonEntity(
    @PrimaryKey val lessonId: String,
    val categoryId: String,
    val title: String,
    val videoUrl: String, // Có thể là link URL hoặc tên file trong res/raw
    val level: String,
    val duration: String
)

@Entity(
    tableName = "transcripts",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["lessonId"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TranscriptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: String,
    val startTime: Float,
    val endTime: Float,
    val text: String,
    val orderIndex: Int // Thứ tự của câu trong bài
)
