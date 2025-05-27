package com.example.finalproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class RegexTesterViewModel(
    private val repository: HistoryRepository
) : ViewModel() {
    
    val regexPattern = MutableLiveData<String>()
    val testText = MutableLiveData<String>()
    val matches = MutableLiveData<List<String>>()
    val isValid = MutableLiveData<Boolean?>()
    val errorMessage = MutableLiveData<String>()
    val matchCount = MutableLiveData<Int>()
    
    fun testRegex(pattern: String, text: String) {
        if (pattern.isEmpty()) {
            errorMessage.value = "Please enter a regex pattern"
            return
        }
        
        if (text.isEmpty()) {
            errorMessage.value = "Please enter test text"
            return
        }
        
        try {
            val regex = Pattern.compile(pattern)
            val matcher = regex.matcher(text)
            val foundMatches = mutableListOf<String>()
            
            while (matcher.find()) {
                foundMatches.add(matcher.group())
            }
            
            matches.value = foundMatches
            matchCount.value = foundMatches.size
            isValid.value = true
            
            viewModelScope.launch {
                repository.addHistory(
                    "Regex Tester", 
                    "Pattern: $pattern\nText: $text", 
                    "Matches: ${foundMatches.size} found"
                )
            }
        } catch (e: PatternSyntaxException) {
            isValid.value = false
            errorMessage.value = "Invalid regex pattern: ${e.description}"
        } catch (e: Exception) {
            isValid.value = false
            errorMessage.value = "Error testing regex: ${e.message}"
        }
    }
    
    fun clearResults() {
        matches.value = emptyList()
        matchCount.value = 0
        isValid.value = null
    }
}