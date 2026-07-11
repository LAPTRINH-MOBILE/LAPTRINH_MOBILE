package com.example.app_wordpulse.features.story

import android.content.Intent
import android.os.Bundle
import android.view.View
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

class StoryLevelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_story_level)

        val mainLayout = findViewById<View>(R.id.main_layout)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Click listeners for level buttons
        findViewById<View>(R.id.btnA1).setOnClickListener { openStoryTopic("A1") }
        findViewById<View>(R.id.btnA2).setOnClickListener { openStoryTopic("A2") }
        findViewById<View>(R.id.btnB1).setOnClickListener { openStoryTopic("B1") }
        findViewById<View>(R.id.btnB2).setOnClickListener { openStoryTopic("B2") }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_stories
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_vocab -> {
                    startActivity(Intent(this, VocabTopicActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_listening -> {
                    startActivity(Intent(this, LessonListActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_stories -> true
                R.id.nav_grammar -> {
                    startActivity(Intent(this, GrammarLevelSelectActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun openStoryTopic(level: String) {
        val intent = Intent(this, StoryTopicActivity::class.java).apply {
            putExtra("LEVEL", level)
        }
        startActivity(intent)
    }
}
