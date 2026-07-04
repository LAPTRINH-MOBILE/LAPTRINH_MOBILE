package com.example.app_wordpulse.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "topic")
data class Topic(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @ColumnInfo(name = "level")
    val level: String? = null,

    @ColumnInfo(name = "topic_name")
    val topicName: String? = null,

    @ColumnInfo(name = "image_data", typeAffinity = ColumnInfo.BLOB)
    val imageData: ByteArray? = null
)
