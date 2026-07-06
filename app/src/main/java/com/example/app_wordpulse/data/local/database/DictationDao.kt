package com.example.app_wordpulse.data.local.database

import androidx.room.*
import com.example.app_wordpulse.data.local.entity.CategoryEntity
import com.example.app_wordpulse.data.local.entity.LessonEntity
import com.example.app_wordpulse.data.local.entity.TranscriptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DictationDao {

    // Lấy tất cả danh mục
    @Query("SELECT * FROM lesson_categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    // Lấy bài học theo danh mục
    @Query("SELECT * FROM lessons WHERE categoryId = :catId")
    fun getLessonsByCategory(catId: String): Flow<List<LessonEntity>>

    // Lấy toàn bộ bài học (bao gồm transcript) - Cần dùng @Relation hoặc Query riêng
    @Query("SELECT * FROM lessons WHERE lessonId = :id")
    suspend fun getLessonById(id: String): LessonEntity?

    @Query("SELECT * FROM transcripts WHERE lessonId = :lessonId ORDER BY orderIndex ASC")
    fun getTranscriptsForLesson(lessonId: String): Flow<List<TranscriptEntity>>

    // Thêm dữ liệu bài học
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranscripts(transcripts: List<TranscriptEntity>)
}
