package com.example.app_wordpulse.features.grammar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.app_wordpulse.MainActivity
import com.example.app_wordpulse.R

class GrammarLevelSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grammar_level_select)

        findViewById<View>(R.id.cardLevelA1).setOnClickListener { openGrammar("A1") }
        findViewById<View>(R.id.cardLevelA2).setOnClickListener { openGrammar("A2") }
        findViewById<View>(R.id.cardLevelB1).setOnClickListener { openGrammar("B1") }
        findViewById<View>(R.id.cardLevelB2).setOnClickListener { openGrammar("B2") }

        findViewById<Button>(R.id.btnBackHome).setOnClickListener {
            val homeIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(homeIntent)
            finish()
        }
    }

    private fun openGrammar(level: String) {
        val grammarIntent = Intent(this, GrammarActivity::class.java).apply {
            putExtra(GrammarActivity.EXTRA_LEVEL, level)
        }
        startActivity(grammarIntent)
        finish()
    }
}
