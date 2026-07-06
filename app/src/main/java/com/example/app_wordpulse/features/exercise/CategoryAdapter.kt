package com.example.app_wordpulse.features.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_wordpulse.R
import com.example.app_wordpulse.data.model.LessonCategory
import com.example.app_wordpulse.data.model.DictationLesson

class CategoryAdapter(
    private var categoriesList: List<LessonCategory>,
    private val onVideoClick: (DictationLesson) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryTitle: TextView = itemView.findViewById(R.id.tvCategoryTitle)
        val tvTotalLessons: TextView = itemView.findViewById(R.id.tvTotalLessons)
        val btnSeeAll: Button = itemView.findViewById(R.id.btnSeeAll)
        val rvHorizontalLessons: RecyclerView = itemView.findViewById(R.id.rvHorizontalLessons)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_row, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int = categoriesList.size

    // Hàm cập nhật dữ liệu mới mà không cần khởi tạo lại Adapter (tối ưu hiệu năng)
    fun updateData(newList: List<LessonCategory>) {
        this.categoriesList = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoriesList[position]

        // 1. Gắn dữ liệu tiêu đề hàng (Ví dụ: Movie short clip)
        holder.tvCategoryTitle.text = category.name
        holder.tvTotalLessons.text = "(${category.lessonCount} bài học)"

        // 2. Thiết lập RecyclerView Con chạy theo CHIỀU NGANG cho từng hàng
        holder.rvHorizontalLessons.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        // 3. Khởi tạo Adapter Con (DictationLessonAdapter) lồng bên trong
        val lessonAdapter = DictationLessonAdapter(category.lessons) { selectedLesson ->
            // Khi click vào video con, truyền dữ liệu ngược về cho Activity xử lý mở Dialog chọn chế độ
            onVideoClick(selectedLesson)
        }
        holder.rvHorizontalLessons.adapter = lessonAdapter

        // Xử lý nút xem tất cả nếu cần
        holder.btnSeeAll.setOnClickListener {
            // Logic mở toàn bộ danh mục
        }
    }
}