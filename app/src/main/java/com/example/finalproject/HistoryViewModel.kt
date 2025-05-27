package com.example.finalproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {
    
    val historyItems = MutableLiveData<List<HistoryItem>>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    
    fun loadHistory() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val items = repository.getAllHistory()
                historyItems.value = items
            } catch (e: Exception) {
                errorMessage.value = "Failed to load history: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            try {
                repository.clearHistory()
                historyItems.value = emptyList()
            } catch (e: Exception) {
                errorMessage.value = "Failed to clear history: ${e.message}"
            }
        }
    }
    
    fun deleteHistoryItem(item: HistoryItem) {
        viewModelScope.launch {
            try {
                repository.deleteHistoryItem(item.id)
                loadHistory() // Refresh the list
            } catch (e: Exception) {
                errorMessage.value = "Failed to delete item: ${e.message}"
            }
        }
    }
}