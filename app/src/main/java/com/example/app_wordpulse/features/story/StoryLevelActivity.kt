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

        // Sử dụng View chung vì các container bây giờ là LinearLayout thay vì MaterialButton
        findViewById<View>(R.id.btnA1).setOnClickListener { openStoryTopic("A1") }
        findViewById<View>(R.id.btnA2).setOnClickListener { openStoryTopic("A2") }
        findViewById<View>(R.id.btnB1).setOnClickListener { openStoryTopic("B1") }
        findViewById<View>(R.id.btnB2).setOnClickListener { openStoryTopic("B2") }

        // Sự kiện quay về trang chủ (Vẫn là MaterialButton)
        findViewById<MaterialButton>(R.id.btnBackHome).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Xóa các màn hình trung gian để tránh lỗi xếp chồng màn hình
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun openStoryTopic(level: String) {
        val intent = Intent(this, StoryTopicActivity::class.java).apply {
            putExtra("LEVEL", level)
        }
        startActivity(intent)
    }
}
