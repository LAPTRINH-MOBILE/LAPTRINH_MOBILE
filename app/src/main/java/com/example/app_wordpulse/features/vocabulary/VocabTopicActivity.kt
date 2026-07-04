package com.example.app_wordpulse.features.vocabulary

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_wordpulse.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VocabTopicActivity : AppCompatActivity() {

    private val viewModel: VocabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocab_topic_vocabulary)

        val rvTopics: RecyclerView = findViewById(R.id.rvTopics)
        rvTopics.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.topics.collectLatest { topics ->
                    if (topics.isEmpty()) {
                        android.util.Log.d("VocabTopicActivity", "No topics found in database")
                    }
                    rvTopics.adapter = VocabAdapter(topics) { topic ->
                        val intent = Intent(this@VocabTopicActivity, FlashcardActivity::class.java)
                        intent.putExtra("TOPIC_NAME", topic)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}
