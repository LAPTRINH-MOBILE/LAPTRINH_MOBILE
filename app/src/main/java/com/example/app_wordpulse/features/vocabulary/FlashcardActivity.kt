package com.example.app_wordpulse.features.vocabulary

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
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

    private companion object {
        const val FLIP_HALF_DURATION = 140L
        const val CARD_CAMERA_DISTANCE = 12000f
    }

    private val viewModel: VocabViewModel by viewModels()
    private var currentWordIndex = 0
    private var wordsList = listOf<Word>()
    private var isShowingDefinition = false
    private var isFlipAnimating = false

    private lateinit var tvCardContent: TextView
    private lateinit var tvProgress: TextView
    private lateinit var tvLevel: TextView
    private lateinit var ivIllustration: ImageView
    private lateinit var cvFlashcard: CardView
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_flashcard_vocabulary)

            tvCardContent = findViewById(R.id.tvCardContent)
            tvProgress = findViewById(R.id.tvProgress)
            tvLevel = findViewById(R.id.tvLevel)
            ivIllustration = findViewById(R.id.ivIllustration)
            cvFlashcard = findViewById(R.id.cvFlashcard)
            cvFlashcard.cameraDistance = CARD_CAMERA_DISTANCE * resources.displayMetrics.density

            btnPrevious = findViewById(R.id.btnPrevious)
            btnNext = findViewById(R.id.btnNext)

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
                flipCard()
            }

            btnNext.setOnClickListener {
                if (currentWordIndex < wordsList.size - 1) {
                    currentWordIndex++
                    resetCardState()
                    showWord(currentWordIndex)
                } else {
                    // When on the last card and clicking "Hoàn thành"
                    finish()
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
        cvFlashcard.animate().cancel()
        cvFlashcard.animate().setListener(null)
        isFlipAnimating = false
        cvFlashcard.isClickable = true
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

    private fun flipCard() {
        if (isFlipAnimating || wordsList.isEmpty()) return

        isFlipAnimating = true
        cvFlashcard.isClickable = false
        cvFlashcard.animate()
            .rotationY(90f)
            .setDuration(FLIP_HALF_DURATION)
            .setInterpolator(AccelerateInterpolator())
            .withLayer()
            .setListener(object : AnimatorListenerAdapter() {
                private var cancelled = false

                override fun onAnimationCancel(animation: Animator) {
                    cancelled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    cvFlashcard.animate().setListener(null)
                    if (cancelled) {
                        finishFlipAnimation()
                        return
                    }

                    isShowingDefinition = !isShowingDefinition
                    updateCardDisplay()
                    cvFlashcard.rotationY = -90f
                    cvFlashcard.animate()
                        .rotationY(0f)
                        .setDuration(FLIP_HALF_DURATION)
                        .setInterpolator(DecelerateInterpolator())
                        .withLayer()
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                cvFlashcard.animate().setListener(null)
                                finishFlipAnimation()
                            }
                        })
                        .start()
                }
            })
            .start()
    }

    private fun finishFlipAnimation() {
        isFlipAnimating = false
        cvFlashcard.isClickable = true
    }

    private fun updateCardDisplay() {
        if (wordsList.isEmpty()) {
            tvProgress.text = "0/0"
            return
        }
        val word = wordsList[currentWordIndex]

        tvCardContent.text = if (isShowingDefinition) word.definition ?: "" else word.term ?: ""
        tvProgress.text = "${currentWordIndex + 1}/${wordsList.size}"
        tvLevel.text = "Cấp độ: ${word.level ?: ""}"

        // Update Next button text if it's the last card
        if (currentWordIndex == wordsList.size - 1) {
            btnNext.text = "Hoàn thành"
        } else {
            btnNext.text = "Tiếp theo"
        }

        if (!word.imageUrl.isNullOrEmpty()) {
            val resId = resources.getIdentifier(word.imageUrl, "drawable", packageName)
            if (resId != 0) {
                ivIllustration.setImageResource(resId)
                ivIllustration.visibility = View.VISIBLE
            } else {
                ivIllustration.visibility = View.GONE
            }
        } else {
            ivIllustration.visibility = View.GONE
        }
    }
}
