package com.example.app_wordpulse.features.vocabulary

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.app_wordpulse.R
import com.example.app_wordpulse.data.model.Word
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FlashcardActivity : AppCompatActivity() {

    private val viewModel: VocabViewModel by viewModels()
    private var currentWordIndex = 0
    private var wordsList = listOf<Word>()
    private var isShowingDefinition = false

    private lateinit var tvCardContent: TextView
    private lateinit var tvProgress: TextView
    private lateinit var tvLevel: TextView
    private lateinit var ivIllustration: ImageView
    private lateinit var cvFlashcard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_flashcard_vocabulary)

            tvCardContent = findViewById(R.id.tvCardContent)
            tvProgress = findViewById(R.id.tvProgress)
            tvLevel = findViewById(R.id.tvLevel)
            ivIllustration = findViewById(R.id.ivIllustration)
            cvFlashcard = findViewById(R.id.cvFlashcard)

            val btnPrevious: Button = findViewById(R.id.btnPrevious)
            val btnNext: Button = findViewById(R.id.btnNext)

            val topicName = intent.getStringExtra("TOPIC_NAME") ?: ""
            supportActionBar?.title = topicName

            viewModel.loadWordsByTopic(topicName)

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.words.collectLatest { words ->
                        wordsList = words
                        if (wordsList.isNotEmpty()) {
                            currentWordIndex = 0
                            resetCardState()
                            showWord(currentWordIndex)
                        } else {
                            currentWordIndex = 0
                            tvProgress.text = "0/0"
                            tvLevel.text = "Level"
                            tvCardContent.text = "Không có dữ liệu"
                        }
                    }
                }
            }

            cvFlashcard.setOnClickListener {
                val rotation = if (isShowingDefinition) 0f else 180f
                cvFlashcard.animate().rotationY(rotation).setDuration(300).withEndAction {
                    isShowingDefinition = !isShowingDefinition
                    tvCardContent.rotationY = if (isShowingDefinition) 180f else 0f
                    tvLevel.rotationY = if (isShowingDefinition) 180f else 0f
                    ivIllustration.rotationY = if (isShowingDefinition) 180f else 0f
                    updateCardDisplay()
                }.start()
            }

            btnNext.setOnClickListener {
                if (currentWordIndex < wordsList.size - 1) {
                    currentWordIndex++
                    resetCardState()
                    showWord(currentWordIndex)
                }
            }

            btnPrevious.setOnClickListener {
                if (currentWordIndex > 0) {
                    currentWordIndex--
                    resetCardState()
                    showWord(currentWordIndex)
                }
            }
        } catch (e: Exception) {
            if (e !is kotlinx.coroutines.CancellationException) {
                e.printStackTrace()
                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun resetCardState() {
        isShowingDefinition = false
        cvFlashcard.rotationY = 0f
        tvCardContent.rotationY = 0f
        tvLevel.rotationY = 0f
        ivIllustration.rotationY = 0f
    }

    private fun showWord(index: Int) {
        currentWordIndex = index
        updateCardDisplay()
    }

    private fun updateCardDisplay() {
        if (wordsList.isEmpty()) {
            tvProgress.text = "0/0"
            return
        }
        val word = wordsList[currentWordIndex]

        tvCardContent.text = if (isShowingDefinition) word.definition else word.term
        tvProgress.text = "${currentWordIndex + 1}/${wordsList.size}"
        tvLevel.text = "Cấp độ: ${word.level}"
        ivIllustration.visibility = View.GONE
    }
}
