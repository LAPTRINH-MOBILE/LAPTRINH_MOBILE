package com.example.app_wordpulse.features.exercise

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.app_wordpulse.R

class LessonListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_list)

        // GIẢ LẬP: Khi người dùng bấm vào một Video trên danh sách RecyclerView
        // Chỗ này trong Adapter bạn sẽ gọi hàm showModeSelectionDialog(videoId)
    }

    private fun showModeSelectionDialog(videoId: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_choose_mode, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val btnDictation = dialogView.findViewById<LinearLayout>(R.id.layoutDictation)
        val btnShadowing = dialogView.findViewById<LinearLayout>(R.id.layoutShadowing)

        // Nếu ấn chọn chế độ Nghe - Chép chính tả
        btnDictation.setOnClickListener {
            builder.dismiss()
            val intent = Intent(this, DictationActivity::class.java).apply {
                putExtra("VIDEO_ID", videoId)
            }
            startActivity(intent)
        }

        // Nếu chọn chế độ Phát âm (Shadowing)
        btnShadowing.setOnClickListener {
            builder.dismiss()
            // Chuyển hướng sang ShadowingActivity tương ứng của bạn
        }

        builder.show()
    }
}