package com.example.app_wordpulse.features.exercise

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_wordpulse.R
import com.example.app_wordpulse.data.model.TranscriptLine
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class DictationActivity : AppCompatActivity() {

    private val viewModel: DictationViewModel by viewModels()
    private var exoPlayer: ExoPlayer? = null
    private var currentLine: TranscriptLine? = null

    private lateinit var edtAnswer: EditText
    private lateinit var chipGroupHints: ChipGroup
    private lateinit var rvTranscriptList: RecyclerView
    private lateinit var transcriptAdapter: TranscriptAdapter
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar

    private val handler = Handler(Looper.getMainLooper())
    private val checkProgressRunnable = object : Runnable {
        override fun run() {
            exoPlayer?.let { player ->
                currentLine?.let { line ->
                    val currentPosSeconds = player.currentPosition / 1000f
                    if (currentPosSeconds >= line.endTime) {
                        player.pause()
                    } else {
                        handler.postDelayed(this, 100)
                    }
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictation)
        initViews()
        setupPlayer()
        setupRecyclerView()
        observeViewModel()
        setupClickListeners()

        val lessonId = intent.getStringExtra("VIDEO_ID") ?: ""
        if (lessonId.isNotEmpty()) {
            viewModel.loadLesson(lessonId)
        }
    }

    private fun initViews() {
        edtAnswer = findViewById(R.id.edtAnswer)
        chipGroupHints = findViewById(R.id.chipGroupHints)
        rvTranscriptList = findViewById(R.id.rvTranscriptList)
        playerView = findViewById(R.id.playerView)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        playerView.player = exoPlayer
        exoPlayer?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) startCheckingProgress() else stopCheckingProgress()
            }
        })
    }

    private fun setupRecyclerView() {
        rvTranscriptList.layoutManager = LinearLayoutManager(this)
        transcriptAdapter = TranscriptAdapter(emptyList()) { index ->
            viewModel.setCurrentSentenceIndex(index)
        }
        rvTranscriptList.adapter = transcriptAdapter
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnReplaySegment).setOnClickListener {
            currentLine?.let { line ->
                exoPlayer?.seekTo((line.startTime * 1000).toLong())
                exoPlayer?.play()
                startCheckingProgress()
            }
        }

        findViewById<Button>(R.id.btnNextSegment).setOnClickListener {
            currentLine?.let { line ->
                val userText = edtAnswer.text.toString()
                if (viewModel.isAnswerCorrect(userText, line.text)) {
                    Toast.makeText(this, "Chính xác!", Toast.LENGTH_SHORT).show()
                    edtAnswer.text.clear()
                    viewModel.moveToNextSentence()
                } else {
                    Toast.makeText(this, "Chưa đúng rồi, hãy nghe lại!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.lessonData.observe(this) { lesson ->
            if (lesson.videoUrl.isNotEmpty()) {
                exoPlayer?.setMediaItem(MediaItem.fromUri(lesson.videoUrl))
                exoPlayer?.prepare()
            }
            transcriptAdapter.setData(lesson.transcripts)
        }

        viewModel.currentSentenceIndex.observe(this) { index ->
            val lesson = viewModel.lessonData.value ?: return@observe
            if (index < lesson.transcripts.size) {
                currentLine = lesson.transcripts[index]
                transcriptAdapter.updateCurrentIndex(index)
                rvTranscriptList.scrollToPosition(index)

                currentLine?.let { line ->
                    exoPlayer?.seekTo((line.startTime * 1000).toLong())
                    exoPlayer?.play()
                    updateHintChips(line.text)
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun startCheckingProgress() {
        handler.removeCallbacks(checkProgressRunnable)
        handler.post(checkProgressRunnable)
    }

    private fun stopCheckingProgress() {
        handler.removeCallbacks(checkProgressRunnable)
    }

    private fun updateHintChips(fullText: String) {
        chipGroupHints.removeAllViews()
        viewModel.getMaskedWordList(fullText).forEach { word ->
            val chip = Chip(this).apply {
                text = word
                isCheckable = false
                isClickable = false
            }
            chipGroupHints.addView(chip)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCheckingProgress()
        exoPlayer?.release()
        exoPlayer = null
    }
}
