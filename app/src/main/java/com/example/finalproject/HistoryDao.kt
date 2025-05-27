package com.example.finalproject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): LiveData<List<HistoryItem>>
    
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    suspend fun getAllHistoryList(): List<HistoryItem>
    
    @Query("SELECT * FROM history WHERE toolName = :toolName ORDER BY timestamp DESC")
    fun getHistoryByTool(toolName: String): LiveData<List<HistoryItem>>
    
    @Query("SELECT * FROM history WHERE toolName = :toolName ORDER BY timestamp DESC")
    suspend fun getHistoryByToolList(toolName: String): List<HistoryItem>
    
    @Insert
    suspend fun insertHistory(item: HistoryItem)
    
    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistory(id: Long)
    
    @Query("DELETE FROM history")
    suspend fun clearHistory()
}