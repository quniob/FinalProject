package com.example.finalproject

import android.content.Context

class HistoryRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val historyDao = database.historyDao()
    
    suspend fun getAllHistory(): List<HistoryItem> = historyDao.getAllHistoryList()
    
    suspend fun getHistoryByTool(toolName: String): List<HistoryItem> = 
        historyDao.getHistoryByToolList(toolName)
    
    suspend fun addHistory(toolName: String, input: String, output: String) {
        val item = HistoryItem(
            toolName = toolName,
            input = input,
            output = output
        )
        historyDao.insertHistory(item)
    }
    
    suspend fun deleteHistoryItem(id: Long) = historyDao.deleteHistory(id)
    
    suspend fun clearHistory() = historyDao.clearHistory()
}