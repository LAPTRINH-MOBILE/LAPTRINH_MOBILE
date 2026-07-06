package com.example.app_wordpulse.features.exercise

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_wordpulse.R

class LessonListActivity : AppCompatActivity() {

    private val viewModel: LessonListViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_list)

        val rvLessons: RecyclerView = findViewById(R.id.rvLessons)
        rvLessons.layoutManager = LinearLayoutManager(this)

        categoryAdapter = CategoryAdapter(emptyList()) { lesson ->
            showModeSelectionDialog(lesson.id.ifEmpty { lesson.videoId })
        }
        rvLessons.adapter = categoryAdapter

        viewModel.categories.observe(this) { categories ->
            categoryAdapter.updateData(categories)
        }

        viewModel.loadCategories()
    }

    private fun showModeSelectionDialog(videoId: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_choose_mode, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val btnDictation = dialogView.findViewById<LinearLayout>(R.id.layoutDictation)
        val btnShadowing = dialogView.findViewById<LinearLayout>(R.id.layoutShadowing)

        btnDictation.setOnClickListener {
            builder.dismiss()
            val intent = Intent(this, DictationActivity::class.java).apply {
                putExtra("VIDEO_ID", videoId)
            }
            startActivity(intent)
        }

        btnShadowing.setOnClickListener {
            builder.dismiss()
            // Chuyển sang ShadowingActivity nếu có
        }

        builder.show()
    }
}
