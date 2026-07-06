package com.example.app_wordpulse.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "story",
    foreignKeys = [
        ForeignKey(
            entity = Topic::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class Story(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @ColumnInfo(name = "topic_id")
    val topicId: Int? = null,

    val title: String? = null,

    @ColumnInfo(name = "story_content")
    val storyContent: String? = null,

    @ColumnInfo(name = "question")
    val question: String? = null,

    @ColumnInfo(name = "option_a")
    val optionA: String? = null,

    @ColumnInfo(name = "option_b")
    val optionB: String? = null,

    @ColumnInfo(name = "option_c")
    val optionC: String? = null,

    @ColumnInfo(name = "option_d")
    val optionD: String? = null,

    @ColumnInfo(name = "correct_answer")
    val correctAnswer: String? = null,

    @ColumnInfo(name = "explanation_vi")
    val explanationVi: String? = null
)
