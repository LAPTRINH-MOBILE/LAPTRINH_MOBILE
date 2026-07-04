package com.example.app_wordpulse.features.story

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_wordpulse.R
import com.google.android.material.button.MaterialButton

class StoryResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_story_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val score = intent.getIntExtra("SCORE", 0)
        val total = intent.getIntExtra("TOTAL", 10)
        val topicId = intent.getIntExtra("TOPIC_ID", -1)

        val tvScore = findViewById<TextView>(R.id.tvScore)
        tvScore.text = "$score/$total"

        findViewById<MaterialButton>(R.id.btnRetry).setOnClickListener {
            val intent = Intent(this, StoryQuizActivity::class.java).apply {
                putExtra("TOPIC_ID", topicId)
            }
            startActivity(intent)
            finish()
        }

        findViewById<MaterialButton>(R.id.btnBackToList).setOnClickListener {
            finish()
        }
    }
}