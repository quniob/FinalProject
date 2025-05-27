package com.example.finalproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.security.MessageDigest

class HashGeneratorViewModel(
    private val repository: HistoryRepository
) : ViewModel() {
    
    val inputText = MutableLiveData<String>()
    val md5Hash = MutableLiveData<String>()
    val sha1Hash = MutableLiveData<String>()
    val sha256Hash = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    
    fun generateHashes(input: String) {
        if (input.isEmpty()) {
            errorMessage.value = "Please enter text to hash"
            return
        }
        
        try {
            val md5 = generateHash(input, "MD5")
            val sha1 = generateHash(input, "SHA-1")
            val sha256 = generateHash(input, "SHA-256")
            
            md5Hash.value = md5
            sha1Hash.value = sha1
            sha256Hash.value = sha256
            
            viewModelScope.launch {
                repository.addHistory("Hash Generator", input, "MD5: $md5")
                repository.addHistory("Hash Generator", input, "SHA1: $sha1")
                repository.addHistory("Hash Generator", input, "SHA256: $sha256")
            }
        } catch (e: Exception) {
            errorMessage.value = "Failed to generate hashes: ${e.message}"
        }
    }
    
    private fun generateHash(input: String, algorithm: String): String {
        val digest = MessageDigest.getInstance(algorithm)
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    fun clearHashes() {
        md5Hash.value = ""
        sha1Hash.value = ""
        sha256Hash.value = ""
    }
}