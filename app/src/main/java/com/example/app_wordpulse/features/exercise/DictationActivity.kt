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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictation)

        // Ánh xạ View
        edtAnswer = findViewById(R.id.edtAnswer)
        chipGroupHints = findViewById(R.id.chipGroupHints)
        rvTranscriptList = findViewById(R.id.rvTranscriptList)
        val btnReplay = findViewById<Button>(R.id.btnReplaySegment)
        val btnNext = findViewById<Button>(R.id.btnNextSegment)

        // Cấu hình Youtube Player
        val youtubePlayerView = findViewById<YouTubePlayerView>(R.id.youtubePlayerView)
        lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                mYouTubePlayer = youTubePlayer
                viewModel.loadDummyData() // Kích hoạt nạp dữ liệu bài học
                observeViewModel()
            }

            // LOGIC CỐT LÕI: Đồng bộ hóa thời gian thực
            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                currentLine?.let {
                    if (second >= it.endTime) {
                        youTubePlayer.pause() // Video chạy hết thời lượng câu -> Tự động dừng
                    }
                }
            }
        })

        // Nút bấm phát lại phân đoạn câu hiện tại
        btnReplay.setOnClickListener {
            currentLine?.let { line ->
                mYouTubePlayer?.seekTo(line.startTime)
                mYouTubePlayer?.play()
            }
        }

        // Nút kiểm tra đáp án và chuyển sang câu tiếp theo
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
        // Lắng nghe dữ liệu bài học để cấu hình danh sách RecyclerView phát nhạc
        viewModel.lessonData.observe(this) { lesson ->
            mYouTubePlayer?.cueVideo(lesson.videoId, 0f)

            // Khởi tạo sơ bộ dữ liệu cho danh sách RecyclerView (Cần code thêm Adapter cụ thể sau)
            rvTranscriptList.layoutManager = LinearLayoutManager(this)
        }

        // Lắng nghe sự thay đổi câu hiện tại
        viewModel.currentSentenceIndex.observe(this) { index ->
            val lesson = viewModel.lessonData.value ?: return@observe
            if (index < lesson.transcripts.size) {
                currentLine = lesson.transcripts[index]

                // Ép video tự nhảy về giây bắt đầu phân đoạn câu mới
                currentLine?.let { line ->
                    mYouTubePlayer?.seekTo(line.startTime)
                    mYouTubePlayer?.play()

                    // Cập nhật vùng ChipGroup hiển thị các từ dạng ***
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