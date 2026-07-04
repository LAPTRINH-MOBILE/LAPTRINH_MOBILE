package com.example.app_wordpulse

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
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
        val dialogView = layoutInflater.inflate(R.layout.dialog_grammar_level, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        fun selectLevel(rowId: Int, radioId: Int, level: String) {
            val row = dialogView.findViewById<LinearLayout>(rowId)
            val radio = dialogView.findViewById<RadioButton>(radioId)
            row.setOnClickListener {
                clearLevelSelection(dialogView)
                radio.isChecked = true
                startActivity(
                    Intent(this, GrammarActivity::class.java).apply {
                        putExtra(GrammarActivity.EXTRA_LEVEL, level)
                    }
                )
                dialog.dismiss()
            }
        }

        dialogView.findViewById<ImageButton>(R.id.btnCloseLevelDialog).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.btnCancelLevelDialog).setOnClickListener {
            dialog.dismiss()
        }

        selectLevel(R.id.rowA1, R.id.radioA1, "A1")
        selectLevel(R.id.rowA2, R.id.radioA2, "A2")
        selectLevel(R.id.rowB1, R.id.radioB1, "B1")
        selectLevel(R.id.rowB2, R.id.radioB2, "B2")

        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun clearLevelSelection(dialogView: View) {
        listOf(R.id.radioA1, R.id.radioA2, R.id.radioB1, R.id.radioB2).forEach { radioId ->
            dialogView.findViewById<RadioButton>(radioId).isChecked = false
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
