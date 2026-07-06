package com.example.app_wordpulse.features.exercise

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_wordpulse.R
import com.example.app_wordpulse.data.model.TranscriptLine
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class DictationActivity : AppCompatActivity() {

    private val viewModel: DictationViewModel by viewModels()
    private var mYouTubePlayer: YouTubePlayer? = null
    private var currentLine: TranscriptLine? = null

    private lateinit var edtAnswer: EditText
    private lateinit var chipGroupHints: ChipGroup
    private lateinit var rvTranscriptList: RecyclerView
    private lateinit var transcriptAdapter: TranscriptAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictation)

        edtAnswer = findViewById(R.id.edtAnswer)
        chipGroupHints = findViewById(R.id.chipGroupHints)
        rvTranscriptList = findViewById(R.id.rvTranscriptList)
        val btnReplay = findViewById<Button>(R.id.btnReplaySegment)
        val btnNext = findViewById<Button>(R.id.btnNextSegment)

        rvTranscriptList.layoutManager = LinearLayoutManager(this)
        transcriptAdapter = TranscriptAdapter(emptyList()) { index ->
            viewModel.setCurrentSentenceIndex(index)
        }
        rvTranscriptList.adapter = transcriptAdapter

        val youtubePlayerView = findViewById<YouTubePlayerView>(R.id.youtubePlayerView)
        lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                mYouTubePlayer = youTubePlayer
                val lessonId = intent.getStringExtra("VIDEO_ID") ?: ""
                if (lessonId.isNotEmpty()) {
                    viewModel.loadLesson(lessonId)
                }
                observeViewModel()
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                currentLine?.let {
                    if (second >= it.endTime) {
                        youTubePlayer.pause()
                    }
                }
            }
        })

        btnReplay.setOnClickListener {
            currentLine?.let { line ->
                mYouTubePlayer?.seekTo(line.startTime)
                mYouTubePlayer?.play()
            }
        }

        btnNext.setOnClickListener {
            currentLine?.let { line ->
                val userText = edtAnswer.text.toString()
                if (viewModel.isAnswerCorrect(userText, line.text)) {
                    Toast.makeText(this, "Chính xác!", Toast.LENGTH_SHORT).show()
                    edtAnswer.text.clear()
                    viewModel.moveToNextSentence()
                } else {
                    Toast.makeText(this, "Chưa đúng rồi, hãy nghe lại thử xem!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.lessonData.observe(this) { lesson ->
            mYouTubePlayer?.cueVideo(lesson.videoId, 0f)
            transcriptAdapter.setData(lesson.transcripts)
        }

        viewModel.currentSentenceIndex.observe(this) { index ->
            val lesson = viewModel.lessonData.value ?: return@observe
            if (index < lesson.transcripts.size) {
                currentLine = lesson.transcripts[index]
                transcriptAdapter.updateCurrentIndex(index)
                rvTranscriptList.scrollToPosition(index)

                currentLine?.let { line ->
                    mYouTubePlayer?.seekTo(line.startTime)
                    mYouTubePlayer?.play()
                    updateHintChips(line.text)
                }
            }
        }
    }

    private fun updateHintChips(fullText: String) {
        chipGroupHints.removeAllViews()
        val maskedWords = viewModel.getMaskedWordList(fullText)
        for (word in maskedWords) {
            val chip = Chip(this)
            chip.text = word
            chip.isCheckable = false
            chip.isClickable = false
            chipGroupHints.addView(chip)
        }
    }
}
