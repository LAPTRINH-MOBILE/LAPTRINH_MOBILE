package com.example.app_wordpulse.features.story

import android.speech.tts.TextToSpeech
import java.util.Locale
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.app_wordpulse.R
import com.example.app_wordpulse.data.local.database.AppDatabase
import com.example.app_wordpulse.data.model.Story
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryQuizActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null

    private var questionList: List<Story> = emptyList()
    private var currentIndex = 0
    private var score = 0
    private var isAnswered = false

    private lateinit var tvProgress: TextView
    private lateinit var articleTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var radios: List<RadioButton>
    private lateinit var cards: List<MaterialCardView>
    private lateinit var explanationLayout: LinearLayout
    private lateinit var tvResultStatus: TextView
    private lateinit var tvExplanation: TextView
    private lateinit var actionButton: MaterialButton
    private lateinit var reminderCard: MaterialCardView
    private lateinit var btnTTS: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_story_quiz)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Ẩn tiêu đề mặc định để dùng TextView căn giữa
        toolbar.setNavigationOnClickListener { showExitDialog() }

        // Xử lý nút back hệ thống
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })

        initViews()
        tts = TextToSpeech(this, this)

        val topicId = intent.getIntExtra("TOPIC_ID", -1)
        if (topicId != -1) {
            loadQuestionsFromDb(topicId)
        } else {
            Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initViews() {
        tvProgress = findViewById(R.id.tvProgress)
        articleTextView = findViewById(R.id.articleTextView)
        questionTextView = findViewById(R.id.questionTextView)
        explanationLayout = findViewById(R.id.explanationLayout)
        tvResultStatus = findViewById(R.id.tvResultStatus)
        tvExplanation = findViewById(R.id.tvExplanation)
        actionButton = findViewById(R.id.actionButton)
        reminderCard = findViewById(R.id.reminderCard)
        btnTTS = findViewById(R.id.btnTTS)

        btnTTS.setOnClickListener {
            val story = questionList.getOrNull(currentIndex)
            if (story != null) {
                val textToRead = "${story.storyContent}. ${story.question}"
                tts?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "StoryTTS")
            }
        }

        radios = listOf(
            findViewById(R.id.radioA),
            findViewById(R.id.radioB),
            findViewById(R.id.radioC),
            findViewById(R.id.radioD)
        )
        cards = listOf(
            findViewById(R.id.cardA),
            findViewById(R.id.cardB),
            findViewById(R.id.cardC),
            findViewById(R.id.cardD)
        )

        radios.forEachIndexed { index, radioButton ->
            val clickListener = View.OnClickListener {
                if (isAnswered) return@OnClickListener
                radios.forEach { it.isChecked = false }
                radioButton.isChecked = true
                updateCardSelection(index)
                reminderCard.visibility = View.GONE
            }
            cards[index].setOnClickListener(clickListener)
            radioButton.setOnClickListener(clickListener)
        }

        actionButton.setOnClickListener {
            if (!isAnswered) {
                checkAnswer()
            } else {
                nextQuestion()
            }
        }
    }

    private fun loadQuestionsFromDb(topicId: Int) {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val stories = withContext(Dispatchers.IO) {
                db.storyDao().getStoriesByTopic(topicId)
            }
            if (stories.isNotEmpty()) {
                questionList = stories.shuffled().take(10)
                displayQuestion()
            } else {
                Toast.makeText(this@StoryQuizActivity, "Chủ đề này chưa có dữ liệu", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun displayQuestion() {
        isAnswered = false
        actionButton.text = "Kiểm tra"
        explanationLayout.visibility = View.GONE
        tvProgress.text = "Câu: ${currentIndex + 1}/${questionList.size}"

        radios.forEach { it.isChecked = false }
        cards.forEach {
            it.setStrokeColor("#E0E0E0".toColorInt())
            it.setCardBackgroundColor(Color.WHITE)
        }

        val currentStory = questionList[currentIndex]

        articleTextView.text = currentStory.storyContent
        questionTextView.text = currentStory.question
        radios[0].text = "A. ${currentStory.optionA}"
        radios[1].text = "B. ${currentStory.optionB}"
        radios[2].text = "C. ${currentStory.optionC}"
        radios[3].text = "D. ${currentStory.optionD}"
    }

    private fun updateCardSelection(selectedIndex: Int) {
        cards.forEachIndexed { index, card ->
            if (index == selectedIndex) {
                card.setStrokeColor("#79B8F4".toColorInt())
                card.setCardBackgroundColor("#E3F2FD".toColorInt())
                // Cập nhật màu chấm tròn của RadioButton sang xanh
                radios[index].buttonTintList = ColorStateList.valueOf("#79B8F4".toColorInt())
            } else {
                card.setStrokeColor("#E0E0E0".toColorInt())
                card.setCardBackgroundColor(Color.WHITE)
                // Reset màu chấm tròn về mặc định (hoặc giữ màu xanh)
                radios[index].buttonTintList = ColorStateList.valueOf("#79B8F4".toColorInt())
            }
        }
    }

    private fun checkAnswer() {
        val selectedIndex = radios.indexOfFirst { it.isChecked }
        if (selectedIndex == -1) {
            reminderCard.visibility = View.VISIBLE
            // Tự động ẩn sau 3 giây
            reminderCard.postDelayed({
                reminderCard.visibility = View.GONE
            }, 3000)
            return
        }

        isAnswered = true
        actionButton.text = "Tiếp tục"
        explanationLayout.visibility = View.VISIBLE

        val currentStory = questionList[currentIndex]

        val selectedLetter = arrayOf("A", "B", "C", "D")[selectedIndex]

        val selectedContent = when (selectedIndex) {
            0 -> currentStory.optionA
            1 -> currentStory.optionB
            2 -> currentStory.optionC
            3 -> currentStory.optionD
            else -> ""
        }?.trim()

        val dbCorrectAnswer = currentStory.correctAnswer?.trim()

        val isCorrect = selectedLetter.equals(dbCorrectAnswer, ignoreCase = true) ||
                selectedContent.equals(dbCorrectAnswer, ignoreCase = true)

        if (isCorrect) {
            score++
            tvResultStatus.text = "CHÍNH XÁC! 🎉"
            tvResultStatus.setTextColor(Color.parseColor("#2ECC71"))
            cards[selectedIndex].setCardBackgroundColor(Color.parseColor("#D5F5E3"))
            cards[selectedIndex].setStrokeColor(Color.parseColor("#2ECC71"))
        } else {
            tvResultStatus.text = "SAI RỒI! ❌"
            tvResultStatus.setTextColor(Color.parseColor("#E74C3C"))
            cards[selectedIndex].setCardBackgroundColor(Color.parseColor("#FADBD8"))
            cards[selectedIndex].setStrokeColor(Color.parseColor("#E74C3C"))

            val correctIndex = when {
                "A".equals(dbCorrectAnswer, ignoreCase = true) || currentStory.optionA?.trim().equals(dbCorrectAnswer, ignoreCase = true) -> 0
                "B".equals(dbCorrectAnswer, ignoreCase = true) || currentStory.optionB?.trim().equals(dbCorrectAnswer, ignoreCase = true) -> 1
                "C".equals(dbCorrectAnswer, ignoreCase = true) || currentStory.optionC?.trim().equals(dbCorrectAnswer, ignoreCase = true) -> 2
                "D".equals(dbCorrectAnswer, ignoreCase = true) || currentStory.optionD?.trim().equals(dbCorrectAnswer, ignoreCase = true) -> 3
                else -> -1
            }

            if (correctIndex != -1) {
                cards[correctIndex].setStrokeColor(Color.parseColor("#2ECC71"))
            }
        }

        tvExplanation.text = "Giải thích: ${currentStory.explanationVi ?: "Không có giải thích"}"
    }

    private fun nextQuestion() {
        currentIndex++
        if (currentIndex < questionList.size) {
            displayQuestion()
        } else {
            val intent = Intent(this, StoryResultActivity::class.java).apply {
                putExtra("SCORE", score)
                putExtra("TOTAL", questionList.size)
                putExtra("TOPIC_ID", intent.getIntExtra("TOPIC_ID", -1))
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Ngôn ngữ không được hỗ trợ", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Khởi tạo TTS thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
    }

    private fun showExitDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Xác nhận thoát")
            .setMessage("Nếu quay lại, tiến độ bài thi hiện tại của bạn sẽ không được lưu. Bạn vẫn muốn thoát chứ?")
            .setIcon(R.drawable.ic_reminder)
            .setPositiveButton("Đồng ý") { d, _ ->
                finish()
            }
            .setNegativeButton("Hủy") { d, _ ->
                d.dismiss()
            }
            .setNeutralButton("Để sau") { d, _ ->
                d.dismiss()
            }
            .setCancelable(false)
            .create()

        dialog.show()

        val mainColor = Color.parseColor("#79B8F4")
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mainColor)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mainColor)
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(mainColor)
    }
}