package com.example.app_wordpulse.data.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class TranscriptLine(
    var id: Int = 0,
    var startTime: Float = 0f,
    var endTime: Float = 0f,
    var text: String = "",
    var translation: String = ""
)

@IgnoreExtraProperties
data class DictationLesson(
    var id: String = "",
    var videoId: String = "",
    var videoUrl: String = "",
    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl") var imageUrl: String = "",
    var thumbnailUrl: String = "",
    var title: String = "",
    var level: String = "",
    var duration: String = "",
    var views: Int = 0,
    @get:PropertyName("isPro") @set:PropertyName("isPro") var isPro: Boolean = false,
    @get:PropertyName("isCompleted") @set:PropertyName("isCompleted") var isCompleted: Boolean = false,
    var dictationDone: Boolean = false,
    var shadowingDone: Boolean = false,
    var transcripts: List<TranscriptLine> = emptyList()
)

@IgnoreExtraProperties
data class LessonCategory(
    var id: String = "",
    var name: String = "",
    var lessonCount: Int = 0,
    var lessons: List<DictationLesson> = emptyList()
)
