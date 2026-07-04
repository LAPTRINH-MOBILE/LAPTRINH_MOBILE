package com.example.app_wordpulse.features.grammar

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_wordpulse.R
import com.example.app_wordpulse.data.model.GrammarExercise

class GrammarActivity : AppCompatActivity() {
    private val viewModel: GrammarViewModel by viewModels()

    private var currentExercise: GrammarExercise? = null
    private var selectedTopicId = NO_TOPIC_ID
    private var selectedLevel: String? = null
    private var hasNavigatedToResult = false

    private lateinit var backButton: ImageButton
    private lateinit var progressTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var scoreTextView: TextView
    private lateinit var grammarTopicTextView: TextView
    private lateinit var vietnameseSentenceTextView: TextView
    private lateinit var translationEditText: EditText
    private lateinit var checkButton: Button
    private lateinit var nextButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grammar)

        bindViews()
        setupListeners()
        observeViewModel()

        selectedTopicId = intent.getIntExtra(
            EXTRA_TOPIC_ID,
            intent.getIntExtra(EXTRA_TOPIC_ID_UPPERCASE, NO_TOPIC_ID)
        )
        selectedLevel = intent.getStringExtra(EXTRA_LEVEL)
        viewModel.loadExercises(selectedTopicId, selectedLevel)
    }

    private fun bindViews() {
        backButton = findViewById(R.id.btnBack)
        progressTextView = findViewById(R.id.tvProgress)
        progressBar = findViewById(R.id.progressBar)
        scoreTextView = findViewById(R.id.tvScore)
        grammarTopicTextView = findViewById(R.id.tvGrammarTopic)
        vietnameseSentenceTextView = findViewById(R.id.vietnameseSentenceTextView)
        translationEditText = findViewById(R.id.translationEditText)
        checkButton = findViewById(R.id.checkButton)
        nextButton = findViewById(R.id.nextButton)
        resultTextView = findViewById(R.id.resultTextView)
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
            scoreTextView.text = score
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
        checkButton.isEnabled = true
        checkButton.visibility = View.VISIBLE
        nextButton.isEnabled = false
        nextButton.visibility = View.GONE
        nextButton.text = if (viewModel.isLastExercise) "Xem kết quả" else "Câu tiếp theo"
        hideFeedback()
        setInputBorderColor(R.color.sky_blue)
    }

    private fun showFeedback(exercise: GrammarExercise, isCorrect: Boolean) {
        val statusText = if (isCorrect) {
            "Chính xác!"
        } else {
            "Chưa đúng.\nĐáp án đúng: ${exercise.correctAnswer}"
        }

        resultTextView.text = "$statusText\n\nGiải thích: ${exercise.explanationVi}"
        resultTextView.visibility = View.VISIBLE
        setInputBorderColor(if (isCorrect) R.color.vista_blue else R.color.error)
    }

    private fun hideFeedback() {
        resultTextView.visibility = View.GONE
        resultTextView.text = ""
        setInputBorderColor(R.color.sky_blue)
    }

    private fun showEmptyState() {
        grammarTopicTextView.text = "Viết và Ngữ pháp"
        vietnameseSentenceTextView.text = "Cấp độ này chưa có dữ liệu bài tập."
        translationEditText.text.clear()
        disableExerciseInput()
        resultTextView.visibility = View.GONE
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

    companion object {
        const val EXTRA_TOPIC_ID = "topic_id"
        const val EXTRA_LEVEL = "level"
        private const val EXTRA_TOPIC_ID_UPPERCASE = "TOPIC_ID"
        private const val NO_TOPIC_ID = -1
    }
}
