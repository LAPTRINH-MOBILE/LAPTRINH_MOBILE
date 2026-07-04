package com.example.app_wordpulse.features.story

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_wordpulse.R
import com.google.android.material.button.MaterialButton

class StoryLevelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_story_level)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<MaterialButton>(R.id.btnA1).setOnClickListener { openStoryTopic("A1") }
        findViewById<MaterialButton>(R.id.btnA2).setOnClickListener { openStoryTopic("A2") }
        findViewById<MaterialButton>(R.id.btnB1).setOnClickListener { openStoryTopic("B1") }
        findViewById<MaterialButton>(R.id.btnB2).setOnClickListener { openStoryTopic("B2") }
    }

    private fun openStoryTopic(level: String) {
        val intent = Intent(this, StoryTopicActivity::class.java).apply {
            putExtra("LEVEL", level)
        }
        startActivity(intent)
    }
}