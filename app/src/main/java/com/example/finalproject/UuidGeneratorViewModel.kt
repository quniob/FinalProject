package com.example.finalproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID

class UuidGeneratorViewModel(
    private val repository: HistoryRepository
) : ViewModel() {
    
    val generatedUuid = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    
    fun generateUuid() {
        try {
            val uuid = UUID.randomUUID().toString()
            generatedUuid.value = uuid
            
            viewModelScope.launch {
                repository.addHistory("UUID Generator", "", uuid)
            }
        } catch (e: Exception) {
            errorMessage.value = "Failed to generate UUID: ${e.message}"
        }
    }
    
    fun generateUppercaseUuid() {
        try {
            val uuid = UUID.randomUUID().toString().uppercase()
            generatedUuid.value = uuid
            
            viewModelScope.launch {
                repository.addHistory("UUID Generator", "uppercase", uuid)
            }
        } catch (e: Exception) {
            errorMessage.value = "Failed to generate UUID: ${e.message}"
        }
    }
    
    fun generateUuidWithoutDashes() {
        try {
            val uuid = UUID.randomUUID().toString().replace("-", "")
            generatedUuid.value = uuid
            
            viewModelScope.launch {
                repository.addHistory("UUID Generator", "no dashes", uuid)
            }
        } catch (e: Exception) {
            errorMessage.value = "Failed to generate UUID: ${e.message}"
        }
    }
}