package com.example.app_wordpulse.features.grammar

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.app_wordpulse.R
import com.example.app_wordpulse.data.model.GrammarExercise
import com.google.android.material.card.MaterialCardView
import java.util.Locale

class GrammarActivity : AppCompatActivity() {
    private val viewModel: GrammarViewModel by viewModels()

    private var currentExercise: GrammarExercise? = null
    private var selectedTopicId = NO_TOPIC_ID
    private var selectedLevel: String? = null
    private var hasNavigatedToResult = false
    private var textToSpeech: TextToSpeech? = null
    private var isTextToSpeechReady = false

    private lateinit var backButton: ImageButton
    private lateinit var speakerButton: ImageButton
    private lateinit var progressTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var scoreTextView: TextView
    private lateinit var grammarTopicTextView: TextView
    private lateinit var vietnameseSentenceTextView: TextView
    private lateinit var translationEditText: EditText
    private lateinit var charCounterTextView: TextView
    private lateinit var hintCard: MaterialCardView
    private lateinit var hintTextView: TextView
    private lateinit var feedbackCard: MaterialCardView
    private lateinit var feedbackIcon: ImageView
    private lateinit var feedbackTitleTextView: TextView
    private lateinit var correctAnswerTextView: TextView
    private lateinit var explanationCard: MaterialCardView
    private lateinit var explanationTextView: TextView
    private lateinit var checkButton: Button
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grammar)

        bindViews()
        setupListeners()
        observeViewModel()
        setupTextToSpeech()

        selectedTopicId = intent.getIntExtra(
            EXTRA_TOPIC_ID,
            intent.getIntExtra(EXTRA_TOPIC_ID_UPPERCASE, NO_TOPIC_ID)
        )
        selectedLevel = intent.getStringExtra(EXTRA_LEVEL)
        viewModel.loadExercises(selectedTopicId, selectedLevel)
    }

    private fun bindViews() {
        backButton = findViewById(R.id.btnBack)
        speakerButton = findViewById(R.id.speakerButton)
        progressTextView = findViewById(R.id.tvProgress)
        progressBar = findViewById(R.id.progressBar)
        scoreTextView = findViewById(R.id.tvScore)
        grammarTopicTextView = findViewById(R.id.tvGrammarTopic)
        vietnameseSentenceTextView = findViewById(R.id.vietnameseSentenceTextView)
        translationEditText = findViewById(R.id.translationEditText)
        charCounterTextView = findViewById(R.id.tvCharCounter)
        hintCard = findViewById(R.id.hintCard)
        hintTextView = findViewById(R.id.tvHint)
        feedbackCard = findViewById(R.id.feedbackCard)
        feedbackIcon = findViewById(R.id.feedbackIcon)
        feedbackTitleTextView = findViewById(R.id.tvFeedbackTitle)
        correctAnswerTextView = findViewById(R.id.tvCorrectAnswer)
        explanationCard = findViewById(R.id.explanationCard)
        explanationTextView = findViewById(R.id.tvExplanation)
        checkButton = findViewById(R.id.checkButton)
        nextButton = findViewById(R.id.nextButton)
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            handleExitRequest()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleExitRequest()
            }
        })

        speakerButton.setOnClickListener {
            speakAnswerAfterCheck()
        }

        translationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                charCounterTextView.text = "${s?.length ?: 0}/$MAX_ANSWER_LENGTH"
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        checkButton.setOnClickListener {
            if (viewModel.isSessionFinished.value == true) return@setOnClickListener
            val exercise = currentExercise ?: return@setOnClickListener
            val userInput = translationEditText.text.toString()

            if (userInput.isBlank()) {
                Toast.makeText(this, "Vui lòng nhập câu dịch tiếng Anh.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isCorrect = viewModel.checkGrammar(userInput, exercise.correctAnswer)
            showFeedback(exercise, isCorrect)
            nextButton.isEnabled = true
            nextButton.text = if (viewModel.isLastExercise) "Xem kết quả" else "Câu tiếp theo"
            checkButton.visibility = View.GONE
            nextButton.visibility = View.VISIBLE
        }

        nextButton.setOnClickListener {
            if (viewModel.isSessionFinished.value == true) return@setOnClickListener
            hideFeedback()
            viewModel.nextExercise()
        }
    }

    private fun observeViewModel() {
        viewModel.currentExercise.observe(this) { exercise ->
            currentExercise = exercise
            if (exercise != null) {
                showExercise(exercise)
            } else if (viewModel.isSessionFinished.value != true) {
                showEmptyState()
            }
        }

        viewModel.progressText.observe(this) { progress ->
            progressTextView.text = progress
        }

        viewModel.progressPercent.observe(this) { percent ->
            progressBar.progress = percent
        }

        viewModel.scoreText.observe(this) { score ->
            updateScoreText(score)
        }

        viewModel.isSessionFinished.observe(this) { isFinished ->
            if (isFinished) {
                disableExerciseInput()
                openResultScreen()
            }
        }
    }

    private fun showExercise(exercise: GrammarExercise) {
        grammarTopicTextView.text = "${exercise.level} • ${exercise.grammarTopic}"
        vietnameseSentenceTextView.text = exercise.vietnameseSentence
        translationEditText.text.clear()
        translationEditText.isEnabled = true
        charCounterTextView.text = "0/$MAX_ANSWER_LENGTH"
        hintTextView.text = getHintForTopic(exercise.grammarTopic)
        hintCard.visibility = View.VISIBLE
        feedbackCard.visibility = View.GONE
        explanationCard.visibility = View.GONE
        checkButton.isEnabled = true
        checkButton.visibility = View.VISIBLE
        nextButton.isEnabled = false
        nextButton.visibility = View.GONE
        nextButton.text = if (viewModel.isLastExercise) "Xem kết quả" else "Câu tiếp theo"
        hideFeedback()
        setInputBorderColor(R.color.sky_blue)
    }

    private fun showFeedback(exercise: GrammarExercise, isCorrect: Boolean) {
        val statusColor = getColor(if (isCorrect) R.color.strength_strong else R.color.error)
        val feedbackBackground = if (isCorrect) "#EAF8EF" else "#FDECEF"

        hintCard.visibility = View.GONE
        feedbackCard.visibility = View.VISIBLE
        explanationCard.visibility = View.VISIBLE
        feedbackCard.setCardBackgroundColor(Color.parseColor(feedbackBackground))
        feedbackIcon.setImageResource(if (isCorrect) R.drawable.ic_check_circle else R.drawable.ic_error_circle)
        feedbackIcon.setColorFilter(statusColor)
        feedbackTitleTextView.text = if (isCorrect) "Chính xác!" else "Chưa đúng."
        feedbackTitleTextView.setTextColor(statusColor)
        correctAnswerTextView.text = "Đáp án đúng: ${exercise.correctAnswer}"
        explanationTextView.text = exercise.explanationVi
        setInputBorderColor(if (isCorrect) R.color.vista_blue else R.color.error)
    }

    private fun hideFeedback() {
        feedbackCard.visibility = View.GONE
        explanationCard.visibility = View.GONE
        hintCard.visibility = if (currentExercise == null) View.GONE else View.VISIBLE
        setInputBorderColor(R.color.sky_blue)
    }

    private fun showEmptyState() {
        grammarTopicTextView.text = "Viết và Ngữ pháp"
        vietnameseSentenceTextView.text = "Cấp độ này chưa có dữ liệu bài tập."
        translationEditText.text.clear()
        disableExerciseInput()
        hintCard.visibility = View.GONE
        feedbackCard.visibility = View.GONE
        explanationCard.visibility = View.GONE
    }

    private fun disableExerciseInput() {
        translationEditText.isEnabled = false
        checkButton.isEnabled = false
        checkButton.visibility = View.GONE
        nextButton.isEnabled = false
        nextButton.visibility = View.GONE
    }

    private fun openResultScreen() {
        if (hasNavigatedToResult) return
        hasNavigatedToResult = true

        val intent = Intent(this, GrammarResultActivity::class.java).apply {
            putExtra(GrammarResultActivity.EXTRA_TOTAL_COUNT, viewModel.totalCount)
            putExtra(GrammarResultActivity.EXTRA_CORRECT_COUNT, viewModel.correctAnswerCount)
            putExtra(GrammarResultActivity.EXTRA_WRONG_COUNT, viewModel.wrongAnswerCount)
            putExtra(EXTRA_TOPIC_ID, selectedTopicId)
            selectedLevel?.let { putExtra(EXTRA_LEVEL, it) }
        }
        startActivity(intent)
        finish()
    }

    private fun handleExitRequest() {
        val isInActiveSession = currentExercise != null && viewModel.isSessionFinished.value != true
        if (isInActiveSession) {
            showExitDialog()
        } else {
            finish()
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Bạn có muốn thoát?")
            .setMessage("Kết quả sẽ không được lưu.")
            .setPositiveButton("Thoát") { _, _ -> finish() }
            .setNegativeButton("Tiếp tục", null)
            .show()
    }

    private fun setupTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            isTextToSpeechReady = status == TextToSpeech.SUCCESS
            if (isTextToSpeechReady) {
                textToSpeech?.language = Locale.US
            }
        }
    }

    private fun speakAnswerAfterCheck() {
        val answer = currentExercise?.correctAnswer.orEmpty()
        if (feedbackCard.visibility != View.VISIBLE) {
            Toast.makeText(this, "Kiểm tra câu trả lời trước khi nghe đáp án.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isTextToSpeechReady || answer.isBlank()) {
            Toast.makeText(this, "Chưa sẵn sàng phát âm.", Toast.LENGTH_SHORT).show()
            return
        }

        textToSpeech?.speak(answer, TextToSpeech.QUEUE_FLUSH, null, "grammar-answer")
    }

    private fun updateScoreText(score: String) {
        val styledScore = SpannableString(score)
        val wrongCount = Regex("""Sai:\s*(\d+)""").find(score)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
        val wrongStart = score.indexOf("Sai:")
        if (wrongCount > 0 && wrongStart >= 0) {
            styledScore.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.error)),
                wrongStart,
                score.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        scoreTextView.text = styledScore
    }

    private fun getHintForTopic(grammarTopic: String): String {
        return when (grammarTopic.lowercase(Locale.ROOT)) {
            "present simple" -> "Sử dụng thì hiện tại đơn để diễn tả sự thật hoặc thói quen nhé!"
            "present continuous" -> "Dùng am/is/are + V-ing cho hành động đang diễn ra."
            "past simple" -> "Chú ý dấu hiệu quá khứ và dạng V2/V-ed của động từ."
            "present perfect" -> "Dùng have/has + V3 khi kết quả còn liên quan đến hiện tại."
            "present perfect continuous" -> "Nhấn mạnh hành động kéo dài: have/has been + V-ing."
            "first conditional" -> "Câu điều kiện loại 1: If + hiện tại đơn, will + V."
            "second conditional" -> "Câu điều kiện loại 2: If + quá khứ đơn, would + V."
            "third conditional" -> "Câu điều kiện loại 3: If + had + V3, would have + V3."
            "passive voice" -> "Câu bị động thường dùng be + V3 và nhấn mạnh đối tượng chịu tác động."
            else -> "Đọc kỹ câu tiếng Việt và giữ đúng thì, chủ ngữ, động từ trong câu."
        }
    }

    private fun setInputBorderColor(colorResId: Int) {
        val border = GradientDrawable().apply {
            setColor(getColor(R.color.white))
            cornerRadius = dpToPx(8).toFloat()
            setStroke(dpToPx(2), getColor(colorResId))
        }
        translationEditText.background = border
    }

    private fun dpToPx(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_TOPIC_ID = "topic_id"
        const val EXTRA_LEVEL = "level"
        private const val EXTRA_TOPIC_ID_UPPERCASE = "TOPIC_ID"
        private const val NO_TOPIC_ID = -1
        private const val MAX_ANSWER_LENGTH = 60
    }
}
