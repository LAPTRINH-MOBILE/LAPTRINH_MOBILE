package com.example.app_wordpulse.data.remote

import com.example.app_wordpulse.data.model.Word
import retrofit2.http.GET

interface ApiService {
    @GET("words")
    suspend fun getWords(): List<Word>
}
