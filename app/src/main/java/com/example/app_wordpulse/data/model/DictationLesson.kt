package com.example.app_wordpulse.data.model

data class TranscriptLine(
    val id: Int,
    val startTime: Float,        // Thời gian bắt đầu câu (giây)
    val endTime: Float,          // Thời gian kết thúc câu (giây)
    val text: String             // Câu gốc tiếng Anh đầy đủ
)

data class DictationLesson(
    val videoId: String,          // ID video YouTube (ví dụ: "dQw4w9WgXcQ")
    val title: String,
    val level: String,            // Easy, Normal, Hard
    val transcripts: List<TranscriptLine>
)