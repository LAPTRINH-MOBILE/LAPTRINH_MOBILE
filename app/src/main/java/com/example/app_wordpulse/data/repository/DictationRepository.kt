package com.example.app_wordpulse.data.repository

import com.example.app_wordpulse.data.model.DictationLesson
import com.example.app_wordpulse.data.model.TranscriptLine
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.util.Locale

class DictationRepository {

    private val database = FirebaseDatabase.getInstance("https://app-anh-van-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference("dictation_lessons")

    suspend fun getAllLessons(): List<DictationLesson> {
        val snapshot = database.get().await()
        return snapshot.children.mapNotNull { parseLesson(it) }
    }

    suspend fun getLessonById(lessonId: String): DictationLesson? {
        val snapshot = database.child(lessonId).get().await()
        return if (snapshot.exists()) parseLesson(snapshot) else null
    }

    private fun parseLesson(snapshot: DataSnapshot): DictationLesson {
        val id = snapshot.key ?: ""
        val title = snapshot.child("title").getValue(String::class.java) ?: ""
        val level = snapshot.child("level").getValue(String::class.java) ?: ""
        val views = snapshot.child("views").getValue(Int::class.java) ?: 0
        
        val videoUrl = snapshot.child("videoUrl").getValue(String::class.java) ?:
                       snapshot.child("video_url").getValue(String::class.java) ?:
                       snapshot.child("url").getValue(String::class.java) ?: ""
        
        val thumbnailUrl = snapshot.child("thumbnailUrl").getValue(String::class.java) ?:
                          snapshot.child("imageUrl").getValue(String::class.java) ?:
                          snapshot.child("image").getValue(String::class.java) ?:
                          snapshot.child("thumbnail").getValue(String::class.java) ?: ""

        val transcripts = mutableListOf<TranscriptLine>()
        snapshot.child("transcripts").children.forEach { child ->
            val tId = child.child("id").getValue(Int::class.java) ?: 0
            val start = child.child("startTime").getValue(Float::class.java) ?: 
                       child.child("start_time").getValue(Float::class.java) ?: 0f
            val end = child.child("endTime").getValue(Float::class.java) ?: 
                     child.child("end_time").getValue(Float::class.java) ?: 0f
            val text = child.child("text").getValue(String::class.java) ?: 
                      child.child("content").getValue(String::class.java) ?: ""
            val translation = child.child("translation").getValue(String::class.java) ?: ""
            
            transcripts.add(TranscriptLine(tId, start, end, text, translation))
        }

        var durationValue = snapshot.child("duration").getValue(String::class.java) ?: ""
        if (durationValue.isEmpty() || durationValue == "00:00") {
            if (transcripts.isNotEmpty()) {
                durationValue = formatSeconds(transcripts.last().endTime.toInt())
            }
        }

        return DictationLesson(
            id = id,
            videoId = extractVideoId(videoUrl),
            videoUrl = videoUrl,
            imageUrl = thumbnailUrl,
            thumbnailUrl = thumbnailUrl,
            title = title,
            level = level,
            duration = if (durationValue.isEmpty()) "00:00" else durationValue,
            views = views,
            isPro = snapshot.child("isPro").getValue(Boolean::class.java) ?: false,
            transcripts = transcripts
        )
    }

    private fun extractVideoId(url: String): String {
        return when {
            url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("be/") -> url.substringAfter("be/")
            url.length == 11 -> url
            else -> ""
        }
    }

    private fun formatSeconds(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}
