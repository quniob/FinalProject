package com.example.finalproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UuidGeneratorViewModelFactory(
    private val repository: HistoryRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UuidGeneratorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UuidGeneratorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}