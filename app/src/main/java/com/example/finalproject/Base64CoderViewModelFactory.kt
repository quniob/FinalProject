package com.example.finalproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class Base64CoderViewModelFactory(
    private val repository: HistoryRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Base64CoderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return Base64CoderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}