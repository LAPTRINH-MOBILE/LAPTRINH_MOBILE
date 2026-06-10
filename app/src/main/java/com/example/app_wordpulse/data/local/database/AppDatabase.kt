package com.example.app_wordpulse.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.app_wordpulse.data.model.Word

@Database(entities = [Word::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
