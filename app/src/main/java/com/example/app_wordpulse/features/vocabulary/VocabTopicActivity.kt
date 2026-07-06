package com.example.app_wordpulse.features.vocabulary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_wordpulse.MainActivity
import com.example.app_wordpulse.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VocabTopicActivity : AppCompatActivity() {

    private val viewModel: VocabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocab_topic_vocabulary)

        val levelSelectionView: View = findViewById(R.id.levelSelectionView)
        val topicSelectionView: View = findViewById(R.id.topicSelectionView)
        val btnBackHome: Button = findViewById(R.id.btnBackHome)
        val rvTopics: RecyclerView = findViewById(R.id.rvTopics)

        rvTopics.layoutManager = LinearLayoutManager(this)

        // Setup Level Selection Buttons
        findViewById<View>(R.id.btnLevelA1).setOnClickListener { showTopics("A1") }
        findViewById<View>(R.id.btnLevelA2).setOnClickListener { showTopics("A2") }
        findViewById<View>(R.id.btnLevelB1).setOnClickListener { showTopics("B1") }
        findViewById<View>(R.id.btnLevelB2).setOnClickListener { showTopics("B2") }

        btnBackHome.setOnClickListener {
            if (topicSelectionView.visibility == View.VISIBLE) {
                // If in topic selection, go back to level selection
                topicSelectionView.visibility = View.GONE
                levelSelectionView.visibility = View.VISIBLE
            } else {
                // If in level selection, go back to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.topics.collectLatest { topics ->
                    android.util.Log.d("VocabTopicActivity", "Observed topics: ${topics.size}")
                    rvTopics.adapter = VocabAdapter(topics) { topic ->
                        val intent = Intent(this@VocabTopicActivity, FlashcardActivity::class.java)
                        intent.putExtra("TOPIC_NAME", topic)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun showTopics(level: String) {
        android.util.Log.d("VocabTopicActivity", "showTopics called for level: $level")
        // Toggle views
        findViewById<View>(R.id.levelSelectionView).visibility = View.GONE
        findViewById<View>(R.id.topicSelectionView).visibility = View.VISIBLE
        // Load topics filtered by level
        viewModel.loadTopicsByLevel(level)
    }

    override fun onBackPressed() {
        val topicSelectionView: View = findViewById(R.id.topicSelectionView)
        val levelSelectionView: View = findViewById(R.id.levelSelectionView)
        if (topicSelectionView.visibility == View.VISIBLE) {
            topicSelectionView.visibility = View.GONE
            levelSelectionView.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }
}
