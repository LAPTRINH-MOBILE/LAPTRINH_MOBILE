package com.example.app_wordpulse

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_wordpulse.auth.AuthRepository
import com.example.app_wordpulse.auth.LoginActivity
import com.example.app_wordpulse.features.exercise.LessonListActivity
import com.example.app_wordpulse.features.grammar.GrammarActivity
import com.example.app_wordpulse.features.story.StoryLevelActivity
import com.example.app_wordpulse.features.vocabulary.VocabTopicActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        authRepository = AuthRepository(this)
        val currentUser = authRepository.getCurrentUser()
        
        if (currentUser == null) {
            navigateToLogin()
            return
        }

        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tvHello).text = "Welcome, ${currentUser.username}!"

        setupListeners()
    }

    private fun setupListeners() {
        findViewById<androidx.cardview.widget.CardView>(R.id.btnListening).setOnClickListener {
            android.util.Log.d("MainActivity", "Listening clicked")
            startActivity(Intent(this, LessonListActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnVocab).setOnClickListener {
            android.util.Log.d("MainActivity", "Vocab clicked")
            val intent = Intent(this, VocabTopicActivity::class.java)
            startActivity(intent)
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnStories).setOnClickListener {
            android.util.Log.d("MainActivity", "Stories clicked")
            startActivity(Intent(this, StoryLevelActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnGrammar).setOnClickListener {
            android.util.Log.d("MainActivity", "Grammar clicked")
            showGrammarLevelDialog()
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_topics -> {
                    startActivity(Intent(this, VocabTopicActivity::class.java))
                    true
                }
                R.id.nav_stories -> {
                    startActivity(Intent(this, StoryLevelActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    logout()
                    true
                }
                else -> false
            }
        }
    }

    private fun logout() {
        authRepository.logout()
        navigateToLogin()
    }

    private fun showGrammarLevelDialog() {
        val levels = arrayOf("A1", "A2", "B1", "B2")
        AlertDialog.Builder(this)
            .setTitle("Chọn cấp độ")
            .setItems(levels) { _, which ->
                startActivity(
                    Intent(this, GrammarActivity::class.java).apply {
                        putExtra(GrammarActivity.EXTRA_LEVEL, levels[which])
                    }
                )
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
