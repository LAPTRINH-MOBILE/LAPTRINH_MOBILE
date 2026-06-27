package com.example.app_wordpulse // Thay đổi package tương ứng với dự án của bạn

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.app_wordpulse.R
import com.example.app_wordpulse.features.exercise.DictationActivity
import com.example.app_wordpulse.features.vocabulary.VocabTopicActivity
import com.example.app_wordpulse.features.story.StoryListActivity
import com.example.app_wordpulse.features.grammar.GrammarActivity
import com.example.app_wordpulse.features.auth.LoginActivity
import com.example.app_wordpulse.features.exercise.LessonListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Trong phương thức onCreate của MainActivity.kt
        val btnListening = findViewById<androidx.cardview.widget.CardView>(R.id.btnListening)
        btnListening.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            startActivity(intent)
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnVocab).setOnClickListener {
            val intent = Intent(this, VocabTopicActivity::class.java)
            startActivity(intent)
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnStories).setOnClickListener {
            val intent = Intent(this, StoryListActivity::class.java)
            startActivity(intent)
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnGrammar).setOnClickListener {
            val intent = Intent(this, GrammarActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Đã ở trang chủ
                    true
                }
                R.id.nav_topics -> {
                    startActivity(Intent(this, VocabTopicActivity::class.java))
                    true
                }
                R.id.nav_stories -> {
                    startActivity(Intent(this, StoryListActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    // Ví dụ mở trang Login khi chọn Profile (hoặc tạo ProfileActivity sau)
                    startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}