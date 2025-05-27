package com.example.finalproject

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val toolName: String,
    val input: String,
    val output: String,
    val timestamp: Long = System.currentTimeMillis()
)