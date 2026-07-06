package com.example.app_wordpulse.features.exercise

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Thư viện để load ảnh thumbnail từ link
import com.example.app_wordpulse.R
import com.example.app_wordpulse.data.model.DictationLesson

class DictationLessonAdapter(
    private var lessonsList: List<DictationLesson>,
    private val onLessonItemClick: (DictationLesson) -> Unit // Click để mở màn hình học
) : RecyclerView.Adapter<DictationLessonAdapter.LessonViewHolder>() {

    class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgThumbnail: ImageView = itemView.findViewById(R.id.imgThumbnail)
        val tvProTag: TextView = itemView.findViewById(R.id.tvProTag)
        val tvViews: TextView = itemView.findViewById(R.id.tvViews)
        val tvLevel: TextView = itemView.findViewById(R.id.tvLevel)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDictationStatus: TextView = itemView.findViewById(R.id.tvDictationStatus)
        val tvShadowingStatus: TextView = itemView.findViewById(R.id.tvShadowingStatus)
        val tvYoutubeTag: TextView = itemView.findViewById(R.id.tvYoutubeTag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson_card, parent, false)
        return LessonViewHolder(view)
    }

    override fun getItemCount(): Int = lessonsList.size

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessonsList[position]

        // 1. Đổ dữ liệu text thông thường
        holder.tvTitle.text = lesson.title
        holder.tvLevel.text = lesson.level
        holder.tvViews.text = "🎧 ${lesson.views}" // Dùng trường lesson.views mới
        
        // Hiển thị độ dài bài học
        if (!lesson.duration.isNullOrEmpty() && lesson.duration != "00:00") {
            holder.tvDuration.text = lesson.duration
            holder.tvDuration.visibility = View.VISIBLE
        } else {
            holder.tvDuration.text = "--:--"
            holder.tvDuration.visibility = View.VISIBLE
        }

        // 2. Tải ảnh thumbnail (Chỉ lấy link trực tiếp từ database theo yêu cầu)
        val finalThumbnailUrl = lesson.thumbnailUrl.ifEmpty { lesson.imageUrl }

        // Cập nhật tag nguồn video (Ẩn tag YouTube, chỉ giữ Drive nếu cần)
        if (lesson.videoUrl.contains("drive.google.com")) {
            holder.tvYoutubeTag.visibility = View.VISIBLE
            holder.tvYoutubeTag.text = "🔗 Google Drive"
            holder.tvYoutubeTag.setBackgroundColor(Color.parseColor("#E3F2FD"))
            holder.tvYoutubeTag.setTextColor(Color.parseColor("#1565C0"))
        } else {
            holder.tvYoutubeTag.visibility = View.GONE
        }

        Glide.with(holder.itemView.context)
            .load(finalThumbnailUrl)
            .placeholder(R.drawable.ic_logo) 
            .error(R.drawable.ic_logo)
            .into(holder.imgThumbnail)

        // 3. Ẩn/Hiện tag PRO màu vàng dựa trên data
        holder.tvProTag.visibility = if (lesson.isPro) View.VISIBLE else View.GONE

        // 4. BIẾN ĐỔI MÀU SẮC ĐỘNG THEO CẤP ĐỘ (ĐÂY LÀ LOGIC BƯỚC 4 CỦA BẠN)
        when (lesson.level.uppercase()) {
            "A1" -> {
                holder.tvLevel.setBackgroundColor(Color.parseColor("#E3F2FD")) // Xanh dương nhạt
                holder.tvLevel.setTextColor(Color.parseColor("#1565C0"))
            }
            "A2" -> {
                holder.tvLevel.setBackgroundColor(Color.parseColor("#E8F5E9")) // Xanh lá nhạt
                holder.tvLevel.setTextColor(Color.parseColor("#2E7D32"))
            }
            "B1" -> {
                holder.tvLevel.setBackgroundColor(Color.parseColor("#FFF3E0")) // Cam nhạt
                holder.tvLevel.setTextColor(Color.parseColor("#E65100"))
            }
            "B2" -> {
                holder.tvLevel.setBackgroundColor(Color.parseColor("#EDE7F6")) // Tím nhạt
                holder.tvLevel.setTextColor(Color.parseColor("#5E35B1"))
            }
            else -> {
                holder.tvLevel.setBackgroundColor(Color.parseColor("#F5F5F5")) // Xám mặc định
                holder.tvLevel.setTextColor(Color.parseColor("#616161"))
            }
        }

        // 5. Cập nhật icon ☑ hoặc ⓧ cho trạng thái hoàn thành bài học
        holder.tvDictationStatus.text = if (lesson.dictationDone) "Dictation ☑" else "Dictation ⓧ"
        holder.tvShadowingStatus.text = if (lesson.shadowingDone) "Shadowing ☑" else "Shadowing ⓧ"

        // 6. Xử lý sự kiện click vào item để chuyển màn hình
        holder.itemView.setOnClickListener {
            onLessonItemClick(lesson)
        }
    }

    // Hàm cập nhật lại danh sách khi kéo API thành công
    fun updateData(newLessons: List<DictationLesson>) {
        this.lessonsList = newLessons
        notifyDataSetChanged()
    }
}
