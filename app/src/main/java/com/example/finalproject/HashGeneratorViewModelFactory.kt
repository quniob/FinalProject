package com.example.finalproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HashGeneratorViewModelFactory(
    private val repository: HistoryRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HashGeneratorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HashGeneratorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}