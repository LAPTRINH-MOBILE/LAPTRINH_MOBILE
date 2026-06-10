package com.example.app_wordpulse.features.exercise

import android.content.Context
import android.media.MediaPlayer

class AudioHelper(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(resId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer?.start()
    }
}
