package com.example.app_wordpulse.features.story

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        val imgTopic: ImageView = view.findViewById(R.id.imgTopic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryTopicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story_topic, parent, false)
        return StoryTopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryTopicViewHolder, position: Int) {
        val topic = topicList[position]
        holder.tvTopicName.text = topic.topicName
        
        // Check if imageData is available
        if (topic.imageData != null && topic.imageData.isNotEmpty()) {
            try {
                // Decode the BLOB (ByteArray) into a Bitmap
                val bitmap = BitmapFactory.decodeByteArray(topic.imageData, 0, topic.imageData.size)
                if (bitmap != null) {
                    holder.imgTopic.setImageBitmap(bitmap)
                } else {
                    // If it's not raw image data, try treating it as a resource name string
                    val resourceName = String(topic.imageData).trim().replace("\u0000", "")
                    val context = holder.itemView.context
                    val resId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
                    if (resId != 0) {
                        holder.imgTopic.setImageResource(resId)
                    } else {
                        holder.imgTopic.setImageResource(R.drawable.ic_launcher_background)
                    }
                }
            } catch (e: Exception) {
                holder.imgTopic.setImageResource(R.drawable.ic_launcher_background)
            }
        } else {
            // Default placeholder if no data
            holder.imgTopic.setImageResource(R.drawable.ic_launcher_background)
        }

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
