package com.example.finalproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Base64

class Base64CoderViewModel(
    private val repository: HistoryRepository
) : ViewModel() {
    
    val inputText = MutableLiveData<String>()
    val outputText = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val isEncoding = MutableLiveData<Boolean>(true) // true = encoding, false = decoding
    
    fun encodeText(input: String) {
        if (input.isEmpty()) {
            errorMessage.value = "Please enter text to encode"
            return
        }
        
        try {
            val encoded = Base64.encodeToString(input.toByteArray(), Base64.DEFAULT)
            outputText.value = encoded.trim()
            
            viewModelScope.launch {
                repository.addHistory("Base64 Encoder", input, encoded.trim())
            }
        } catch (e: Exception) {
            errorMessage.value = "Failed to encode: ${e.message}"
        }
    }
    
    fun decodeText(input: String) {
        if (input.isEmpty()) {
            errorMessage.value = "Please enter Base64 text to decode"
            return
        }
        
        try {
            val decoded = Base64.decode(input.trim(), Base64.DEFAULT)
            val decodedString = String(decoded, Charsets.UTF_8)
            outputText.value = decodedString
            
            viewModelScope.launch {
                repository.addHistory("Base64 Decoder", input, decodedString)
            }
        } catch (e: Exception) {
            errorMessage.value = "Failed to decode: Invalid Base64 format"
        }
    }
    
    fun switchMode(encoding: Boolean) {
        isEncoding.value = encoding
        clearOutput()
    }
    
    fun clearOutput() {
        outputText.value = ""
    }
    
    fun clearAll() {
        inputText.value = ""
        outputText.value = ""
    }
}