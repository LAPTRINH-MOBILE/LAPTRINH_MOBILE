package com.example.app_wordpulse.features.story

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_wordpulse.R

class StoryListActivity : AppCompatActivity() {

    private val viewModel: StoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_list)

        val recyclerView = findViewById<RecyclerView>(R.id.storyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.stories.observe(this) { stories ->
            Log.d("StoryListActivity", "Stories updated: ${stories.size} items")
            recyclerView.adapter = StoryAdapter(stories)
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                Log.e("StoryListActivity", "Error: $it")
            }
        }

        viewModel.loadStories()
    }
}
