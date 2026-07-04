package com.example.app_wordpulse.features.story

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_wordpulse.R
import com.example.app_wordpulse.data.local.database.AppDatabase
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryTopicActivity : AppCompatActivity() {

    private lateinit var adapter: StoryTopicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_story_topic)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val level = intent.getStringExtra("LEVEL") ?: "A1"

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Chủ đề - $level"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val rvTopics = findViewById<RecyclerView>(R.id.rvTopics)
        rvTopics.layoutManager = LinearLayoutManager(this)

        adapter = StoryTopicAdapter(emptyList()) { selectedTopic ->
            val intent = Intent(this, StoryQuizActivity::class.java).apply {
                putExtra("TOPIC_ID", selectedTopic.id)
            }
            startActivity(intent)
        }
        rvTopics.adapter = adapter

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            val topics = withContext(Dispatchers.IO) {
                db.topicDao().getTopicsByLevel(level)
            }
            adapter.updateData(topics)
        }
    }
}