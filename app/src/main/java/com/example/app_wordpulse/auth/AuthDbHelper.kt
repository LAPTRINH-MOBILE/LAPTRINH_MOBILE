package com.example.app_wordpulse.auth

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class AuthDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "auth.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD_HASH = "password_hash"
        const val COLUMN_LEVEL = "level"
        const val COLUMN_TOTAL_XP = "total_xp"
        const val COLUMN_CURRENT_STREAK = "current_streak"
        const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID TEXT PRIMARY KEY," +
                "$COLUMN_USERNAME TEXT NOT NULL," +
                "$COLUMN_EMAIL TEXT NOT NULL UNIQUE," +
                "$COLUMN_PASSWORD_HASH TEXT NOT NULL," +
                "$COLUMN_LEVEL INTEGER DEFAULT 1," +
                "$COLUMN_TOTAL_XP INTEGER DEFAULT 0," +
                "$COLUMN_CURRENT_STREAK INTEGER DEFAULT 0," +
                "$COLUMN_CREATED_AT TEXT NOT NULL)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun insertUser(username: String, email: String, passwordHash: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, System.currentTimeMillis().toString())
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email.lowercase(Locale.ROOT).trim())
            put(COLUMN_PASSWORD_HASH, passwordHash)
            put(COLUMN_LEVEL, 1)
            put(COLUMN_TOTAL_XP, 0)
            put(COLUMN_CURRENT_STREAK, 0)
            put(COLUMN_CREATED_AT, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT).format(Date()))
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun findUserByEmail(email: String): StoredUser? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS, null, "$COLUMN_EMAIL = ?",
            arrayOf(email.lowercase(Locale.ROOT).trim()), null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = StoredUser(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH)),
                level = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL)),
                totalXp = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_XP)),
                currentStreak = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_STREAK)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun isEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS, arrayOf(COLUMN_ID), "$COLUMN_EMAIL = ?",
            arrayOf(email.lowercase(Locale.ROOT).trim()), null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Helper for hashing
    fun hashPassword(password: String): String {
        // NOTE: This is a demo-grade implementation.
        // For production, use a strong KDF like Argon2 or BCrypt with a unique salt.
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Intermediate model to hold password hash
    data class StoredUser(
        val id: String,
        val username: String,
        val email: String,
        val passwordHash: String,
        val level: Int,
        val totalXp: Int,
        val currentStreak: Int,
        val createdAt: String
    ) {
        fun toUser() = User(id, username, email, level, totalXp, currentStreak, createdAt)
    }
}
