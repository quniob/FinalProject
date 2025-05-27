package com.example.finalproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RegexTesterViewModelFactory(
    private val repository: HistoryRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegexTesterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegexTesterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}