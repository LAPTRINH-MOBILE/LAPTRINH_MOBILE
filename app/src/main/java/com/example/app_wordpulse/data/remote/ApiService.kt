package com.example.app_wordpulse.data.remote

// Import các model của bạn
import com.example.app_wordpulse.data.model.Word
import com.example.app_wordpulse.data.model.DictationLesson

// Import đầy đủ các thành phần của Retrofit
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("words")
    suspend fun getWords(): List<Word>

    @GET("api/dictation/{id}")
    suspend fun getDictationLesson(@Path("id") lessonId: String): Response<DictationLesson>
}