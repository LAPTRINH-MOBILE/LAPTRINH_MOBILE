package com.example.app_wordpulse.data.local.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.app_wordpulse.data.model.GrammarExercise

class GrammarDbHelper(context: Context) {
    private val appContext = context.applicationContext

    fun getExercisesByTopic(topicId: Int): List<GrammarExercise> {
        return queryExercises(
            """
                SELECT $COLUMN_ID, $COLUMN_TOPIC_ID, $COLUMN_LEVEL, $COLUMN_VIETNAMESE_SENTENCE,
                       $COLUMN_CORRECT_ANSWER, $COLUMN_GRAMMAR_TOPIC, $COLUMN_EXPLANATION_VI
                FROM $TABLE_GRAMMAR_EXERCISE
                WHERE $COLUMN_TOPIC_ID = ?
                ORDER BY $COLUMN_ID
            """.trimIndent(),
            arrayOf(topicId.toString())
        )
    }

    fun getExercisesByLevel(level: String): List<GrammarExercise> {
        return queryExercises(
            """
                SELECT $COLUMN_ID, $COLUMN_TOPIC_ID, $COLUMN_LEVEL, $COLUMN_VIETNAMESE_SENTENCE,
                       $COLUMN_CORRECT_ANSWER, $COLUMN_GRAMMAR_TOPIC, $COLUMN_EXPLANATION_VI
                FROM $TABLE_GRAMMAR_EXERCISE
                WHERE $COLUMN_LEVEL = ? COLLATE NOCASE
                ORDER BY $COLUMN_ID
            """.trimIndent(),
            arrayOf(level)
        )
    }

    fun getExercisesByTopicAndLevel(topicId: Int, level: String): List<GrammarExercise> {
        return queryExercises(
            """
                SELECT $COLUMN_ID, $COLUMN_TOPIC_ID, $COLUMN_LEVEL, $COLUMN_VIETNAMESE_SENTENCE,
                       $COLUMN_CORRECT_ANSWER, $COLUMN_GRAMMAR_TOPIC, $COLUMN_EXPLANATION_VI
                FROM $TABLE_GRAMMAR_EXERCISE
                WHERE $COLUMN_TOPIC_ID = ? AND $COLUMN_LEVEL = ? COLLATE NOCASE
                ORDER BY $COLUMN_ID
            """.trimIndent(),
            arrayOf(topicId.toString(), level)
        )
    }

    fun getExerciseById(id: Int): GrammarExercise? {
        return openReadableDatabase().use { database ->
            val cursor = database.rawQuery(
                """
                    SELECT $COLUMN_ID, $COLUMN_TOPIC_ID, $COLUMN_LEVEL, $COLUMN_VIETNAMESE_SENTENCE,
                           $COLUMN_CORRECT_ANSWER, $COLUMN_GRAMMAR_TOPIC, $COLUMN_EXPLANATION_VI
                    FROM $TABLE_GRAMMAR_EXERCISE
                    WHERE $COLUMN_ID = ?
                    LIMIT 1
                """.trimIndent(),
                arrayOf(id.toString())
            )

            cursor.use {
                if (it.moveToFirst()) it.toGrammarExercise() else null
            }
        }
    }

    fun getAllExercises(): List<GrammarExercise> {
        return queryExercises(
            """
                SELECT $COLUMN_ID, $COLUMN_TOPIC_ID, $COLUMN_LEVEL, $COLUMN_VIETNAMESE_SENTENCE,
                       $COLUMN_CORRECT_ANSWER, $COLUMN_GRAMMAR_TOPIC, $COLUMN_EXPLANATION_VI
                FROM $TABLE_GRAMMAR_EXERCISE
                ORDER BY $COLUMN_ID
            """.trimIndent()
        )
    }

    private fun queryExercises(sql: String, selectionArgs: Array<String>? = null): List<GrammarExercise> {
        return openReadableDatabase().use { database ->
            val cursor = database.rawQuery(sql, selectionArgs)
            cursor.use {
                buildList {
                    while (it.moveToNext()) {
                        add(it.toGrammarExercise())
                    }
                }
            }
        }
    }

    private fun Cursor.toGrammarExercise(): GrammarExercise {
        return GrammarExercise(
            id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
            topicId = getInt(getColumnIndexOrThrow(COLUMN_TOPIC_ID)),
            level = getString(getColumnIndexOrThrow(COLUMN_LEVEL)).orEmpty(),
            vietnameseSentence = getString(getColumnIndexOrThrow(COLUMN_VIETNAMESE_SENTENCE)).orEmpty(),
            correctAnswer = getString(getColumnIndexOrThrow(COLUMN_CORRECT_ANSWER)).orEmpty(),
            grammarTopic = getString(getColumnIndexOrThrow(COLUMN_GRAMMAR_TOPIC)).orEmpty(),
            explanationVi = getString(getColumnIndexOrThrow(COLUMN_EXPLANATION_VI)).orEmpty()
        )
    }

    private fun openReadableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssetsIfNeeded()
        val databaseFile = appContext.getDatabasePath(DATABASE_NAME)
        return SQLiteDatabase.openDatabase(databaseFile.path, null, SQLiteDatabase.OPEN_READONLY)
    }

    private fun copyDatabaseFromAssetsIfNeeded() {
        val databaseFile = appContext.getDatabasePath(DATABASE_NAME)
        if (databaseFile.exists()) return

        databaseFile.parentFile?.mkdirs()
        appContext.assets.open(DATABASE_NAME).use { input ->
            databaseFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    companion object {
        private const val DATABASE_NAME = "dataEl.db"

        private const val TABLE_GRAMMAR_EXERCISE = "grammar_exercise"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TOPIC_ID = "topic_id"
        private const val COLUMN_LEVEL = "level"
        private const val COLUMN_VIETNAMESE_SENTENCE = "vietnamese_sentence"
        private const val COLUMN_CORRECT_ANSWER = "correct_answer"
        private const val COLUMN_GRAMMAR_TOPIC = "grammar_topic"
        private const val COLUMN_EXPLANATION_VI = "explanation_vi"
    }
}
