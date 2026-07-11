package com.example.app_wordpulse.features.story

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_wordpulse.MainActivity
import com.example.app_wordpulse.R
import com.example.app_wordpulse.features.exercise.LessonListActivity
import com.example.app_wordpulse.features.grammar.GrammarLevelSelectActivity
import com.example.app_wordpulse.features.vocabulary.VocabTopicActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class StoryResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_story_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val score = intent.getIntExtra("SCORE", 0)
        val total = intent.getIntExtra("TOTAL", 10)
        val topicId = intent.getIntExtra("TOPIC_ID", -1)

        val tvScore = findViewById<TextView>(R.id.tvScore)
        tvScore.text = "$score/$total"

        val pbCircle = findViewById<android.widget.ProgressBar>(R.id.pbCircle)
        pbCircle.max = total
        pbCircle.progress = score

        findViewById<View>(R.id.btnRetry).setOnClickListener {
            val intent = Intent(this, StoryQuizActivity::class.java).apply {
                putExtra("TOPIC_ID", topicId)
            }
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.btnBackToList).setOnClickListener {
            finish()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_stories

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_vocab -> {
                    val intent = Intent(this, VocabTopicActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_listening -> {
                    val intent = Intent(this, LessonListActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_stories -> true
                R.id.nav_grammar -> {
                    val intent = Intent(this, GrammarLevelSelectActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
