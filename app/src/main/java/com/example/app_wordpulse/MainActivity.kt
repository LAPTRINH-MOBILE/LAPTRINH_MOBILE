package com.example.app_wordpulse

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.app_wordpulse.auth.AuthRepository
import com.example.app_wordpulse.auth.LoginActivity
import com.example.app_wordpulse.features.exercise.LessonListActivity
import com.example.app_wordpulse.features.grammar.GrammarActivity
import com.example.app_wordpulse.features.story.StoryListActivity
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
            startActivity(Intent(this, LessonListActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnVocab).setOnClickListener {
            startActivity(Intent(this, VocabTopicActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnStories).setOnClickListener {
            startActivity(Intent(this, StoryListActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnGrammar).setOnClickListener {
            startActivity(Intent(this, GrammarActivity::class.java))
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
                    startActivity(Intent(this, StoryListActivity::class.java))
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

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
