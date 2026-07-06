package com.example.app_wordpulse.data.model

data class TranscriptLine(
    val id: Int = 0,
    val startTime: Float = 0f,
    val endTime: Float = 0f,
    val text: String = "",
    val translation: String = ""
)

data class DictationLesson(
    val id: String = "",
    val videoId: String = "",
    val videoUrl: String = "",
    val thumbnailUrl: String = "",
    val title: String = "",
    val level: String = "",
    val duration: String = "",
    val views: Int = 0,
    val isPro: Boolean = false,
    val dictationDone: Boolean = false,
    val shadowingDone: Boolean = false,
    val transcripts: List<TranscriptLine> = emptyList()
)

data class LessonCategory(
    val id: String = "",
    val name: String = "",
    val lessonCount: Int = 0,
    val lessons: List<DictationLesson> = emptyList()
)
