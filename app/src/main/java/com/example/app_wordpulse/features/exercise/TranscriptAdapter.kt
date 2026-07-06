package com.example.app_wordpulse.features.exercise

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.app_wordpulse.R
import com.example.app_wordpulse.data.model.TranscriptLine

class TranscriptAdapter(
    private var transcripts: List<TranscriptLine>,
    private var currentIndex: Int = 0,
    private val onLineClick: (Int) -> Unit
) : RecyclerView.Adapter<TranscriptAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIndex: TextView = view.findViewById(R.id.tvIndex)
        val tvMaskedText: TextView = view.findViewById(R.id.tvMaskedText)
        val tvTranslation: TextView = view.findViewById(R.id.tvTranslation)
        val imgStatus: ImageView = view.findViewById(R.id.imgStatus)
        val cardTranscript: View = view.findViewById(R.id.cardTranscript)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transcript, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val line = transcripts[position]
        holder.tvIndex.text = "#${line.id}"

        // Hiển thị lời dịch nếu có
        if (line.translation.isNotEmpty()) {
            holder.tvTranslation.visibility = View.VISIBLE
            holder.tvTranslation.text = line.translation
        } else {
            holder.tvTranslation.visibility = View.GONE
        }
        
        // Logic hiển thị: Nếu đã học qua hoặc đang học thì hiện text (hoặc masked text)
        if (position < currentIndex) {
            holder.tvMaskedText.text = line.text
            holder.tvMaskedText.setTextColor(Color.parseColor("#4CAF50")) // Màu xanh hoàn thành
            holder.imgStatus.setImageResource(android.R.drawable.checkbox_on_background)
            holder.imgStatus.setColorFilter(Color.parseColor("#4CAF50"))
        } else if (position == currentIndex) {
            holder.tvMaskedText.text = "Đang học câu này..."
            holder.tvMaskedText.setTextColor(Color.parseColor("#2196F3"))
            holder.imgStatus.setImageResource(android.R.drawable.ic_media_play)
            holder.imgStatus.setColorFilter(Color.parseColor("#2196F3"))
            holder.cardTranscript.setBackgroundResource(R.drawable.bg_edittext) // Highlight câu đang học
        } else {
            holder.tvMaskedText.text = line.text.split(" ").joinToString(" ") { word ->
                word.map { if (it.isLetterOrDigit()) '*' else it }.joinToString("")
            }
            holder.tvMaskedText.setTextColor(Color.parseColor("#999999"))
            holder.imgStatus.setImageResource(android.R.drawable.ic_menu_edit)
            holder.imgStatus.setColorFilter(Color.parseColor("#999999"))
            holder.cardTranscript.setBackgroundColor(Color.WHITE)
        }

        holder.itemView.setOnClickListener { onLineClick(position) }
    }

    override fun getItemCount() = transcripts.size

    fun updateCurrentIndex(index: Int) {
        currentIndex = index
        notifyDataSetChanged()
    }

    fun setData(newTranscripts: List<TranscriptLine>) {
        transcripts = newTranscripts
        notifyDataSetChanged()
    }
}