package com.example.app_wordpulse.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.app_wordpulse.data.model.Story
import com.example.app_wordpulse.data.model.Topic
import com.example.app_wordpulse.data.model.Word

@Database(entities = [Story::class, Topic::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun topicDao(): TopicDao

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

@Database(entities = [Word::class], version = 2, exportSchema = false)
abstract class VocabularyDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        fun getDatabase(context: Context): VocabularyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabularyDatabase::class.java,
                    "WordPulse.db"
                )
                    .createFromAsset("WordPulse.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
