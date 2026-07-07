package com.example.app_wordpulse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_wordpulse.auth.AuthRepository
import com.example.app_wordpulse.auth.LoginActivity
import com.example.app_wordpulse.features.exercise.LessonListActivity
import com.example.app_wordpulse.features.grammar.GrammarLevelSelectActivity
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

        findViewById<TextView>(R.id.tvHello).text = "Chào bạn, ${currentUser.username}!"

        setupListeners()
    }

    private fun setupListeners() {
        findViewById<View>(R.id.ivAvatar).setOnClickListener {
            showLogoutDialog()
        }

        findViewById<View>(R.id.btnListening).setOnClickListener {
            android.util.Log.d("MainActivity", "Listening clicked")
            startActivity(Intent(this, LessonListActivity::class.java))
        }

        findViewById<View>(R.id.btnVocab).setOnClickListener {
            android.util.Log.d("MainActivity", "Vocab clicked")
            val intent = Intent(this, VocabTopicActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.btnStories).setOnClickListener {
            android.util.Log.d("MainActivity", "Stories clicked")
            startActivity(Intent(this, StoryLevelActivity::class.java))
        }

        findViewById<View>(R.id.btnGrammar).setOnClickListener {
            android.util.Log.d("MainActivity", "Grammar clicked")
            openGrammarLevelSelect()
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_vocab -> {
                    startActivity(Intent(this, VocabTopicActivity::class.java))
                    true
                }
                R.id.nav_listening -> {
                    startActivity(Intent(this, LessonListActivity::class.java))
                    true
                }
                R.id.nav_stories -> {
                    startActivity(Intent(this, StoryLevelActivity::class.java))
                    true
                }
                R.id.nav_grammar -> {
                    openGrammarLevelSelect()
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

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc muốn đăng xuất không?")
            .setPositiveButton("Đăng xuất") { _, _ -> logout() }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun openGrammarLevelSelect() {
        startActivity(Intent(this, GrammarLevelSelectActivity::class.java))
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
