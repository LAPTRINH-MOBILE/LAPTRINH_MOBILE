package com.example.app_wordpulse.features.story

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_wordpulse.R
import com.example.app_wordpulse.data.model.Topic

class StoryTopicAdapter(
    private var topicList: List<Topic>,
    private val onTopicClick: (Topic) -> Unit
) : RecyclerView.Adapter<StoryTopicAdapter.StoryTopicViewHolder>() {

    class StoryTopicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTopicName: TextView = view.findViewById(R.id.tvTopicName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryTopicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story_topic, parent, false)
        return StoryTopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryTopicViewHolder, position: Int) {
        val topic = topicList[position]
        holder.tvTopicName.text = topic.topicName
        holder.itemView.setOnClickListener {
            onTopicClick(topic)
        }
    }

    override fun getItemCount(): Int = topicList.size

    fun updateData(newList: List<Topic>) {
        topicList = newList
        notifyDataSetChanged()
    }
}