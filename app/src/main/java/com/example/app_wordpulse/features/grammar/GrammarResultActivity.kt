package com.example.app_wordpulse.features.grammar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.app_wordpulse.MainActivity
import com.example.app_wordpulse.R

class GrammarResultActivity : AppCompatActivity() {

    private var selectedTopicId = NO_TOPIC_ID
    private var selectedLevel: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grammar_result)

        selectedTopicId = intent.getIntExtra(GrammarActivity.EXTRA_TOPIC_ID, NO_TOPIC_ID)
        selectedLevel = intent.getStringExtra(GrammarActivity.EXTRA_LEVEL)

        val totalCount = intent.getIntExtra(EXTRA_TOTAL_COUNT, 0)
        val correctCount = intent.getIntExtra(EXTRA_CORRECT_COUNT, 0)
        val wrongCount = intent.getIntExtra(EXTRA_WRONG_COUNT, (totalCount - correctCount).coerceAtLeast(0))
        val scorePercent = if (totalCount > 0) {
            (correctCount * 100) / totalCount
        } else {
            0
        }

        findViewById<TextView>(R.id.tvScorePercent).text = "$scorePercent%"
        findViewById<ProgressBar>(R.id.scoreProgressBar).progress = scorePercent
        findViewById<TextView>(R.id.tvTotalValue).text = totalCount.toString()
        findViewById<TextView>(R.id.tvCorrectValue).text = correctCount.toString()
        findViewById<TextView>(R.id.tvWrongValue).text = wrongCount.toString()

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnRetry).setOnClickListener {
            val retryIntent = Intent(this, GrammarActivity::class.java).apply {
                if (selectedTopicId > 0) {
                    putExtra(GrammarActivity.EXTRA_TOPIC_ID, selectedTopicId)
                }
                selectedLevel?.let { putExtra(GrammarActivity.EXTRA_LEVEL, it) }
            }
            startActivity(retryIntent)
            finish()
        }

        findViewById<Button>(R.id.btnHome).setOnClickListener {
            val homeIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(homeIntent)
            finish()
        }
    }

    companion object {
        const val EXTRA_TOTAL_COUNT = "totalCount"
        const val EXTRA_CORRECT_COUNT = "correctCount"
        const val EXTRA_WRONG_COUNT = "wrongCount"
        private const val NO_TOPIC_ID = -1
    }
}
