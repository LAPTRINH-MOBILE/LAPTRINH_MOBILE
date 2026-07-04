package com.example.app_wordpulse.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.app_wordpulse.data.model.Story
import com.example.app_wordpulse.data.model.Topic

@Database(entities = [Story::class, Topic::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun topicDao(): TopicDao // Đảm bảo bạn đã tạo interface này

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dataEl.db"
                )
                    .createFromAsset("dataEl.db")
                    .fallbackToDestructiveMigration() // Sẽ xóa DB cũ và tạo mới khi version thay đổi
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}