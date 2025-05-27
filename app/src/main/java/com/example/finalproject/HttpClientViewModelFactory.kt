package com.example.finalproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HttpClientViewModelFactory(
    private val repository: HistoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HttpClientViewModel::class.java)) {
            return HttpClientViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}