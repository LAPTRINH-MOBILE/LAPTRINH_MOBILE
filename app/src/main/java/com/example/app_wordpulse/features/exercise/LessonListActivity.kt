package com.example.app_wordpulse.features.exercise

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_wordpulse.R

class LessonListActivity : AppCompatActivity() {

    private val viewModel: LessonListViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_list)

        progressBar = findViewById(R.id.progressBar)
        val rvLessons: RecyclerView = findViewById(R.id.rvLessons)
        rvLessons.layoutManager = LinearLayoutManager(this)

        categoryAdapter = CategoryAdapter(emptyList()) { lesson ->
            val intent = Intent(this, DictationActivity::class.java).apply {
                putExtra("VIDEO_ID", lesson.id.ifEmpty { lesson.videoId })
            }
            startActivity(intent)
        }
        rvLessons.adapter = categoryAdapter

        viewModel.categories.observe(this) { categories ->
            categoryAdapter.updateData(categories)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.loadCategories()
    }
}
