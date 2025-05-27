package com.example.finalproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class HttpClientViewModel(private val repository: HistoryRepository) : ViewModel() {
    
    val url = MutableLiveData<String>()
    val method = MutableLiveData<String>()
    val headers = MutableLiveData<String>()
    val requestBody = MutableLiveData<String>()
    val response = MutableLiveData<HttpResponse?>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    init {
        url.value = ""
        method.value = "GET"
        headers.value = ""
        requestBody.value = ""
        isLoading.value = false
    }
    
    fun sendRequest() {
        val requestUrl = url.value ?: ""
        val requestMethod = method.value ?: "GET"
        val requestHeaders = headers.value ?: ""
        val body = requestBody.value ?: ""
        
        if (requestUrl.isBlank()) {
            errorMessage.value = "URL cannot be empty"
            return
        }
        
        isLoading.value = true
        errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val httpResponse = withContext(Dispatchers.IO) {
                    executeRequest(requestUrl, requestMethod, requestHeaders, body)
                }
                response.value = httpResponse
                
                saveToHistory(requestUrl, requestMethod, httpResponse)
                
            } catch (e: Exception) {
                errorMessage.value = "Request failed: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
    
    private suspend fun executeRequest(
        url: String,
        method: String,
        headers: String,
        body: String
    ): HttpResponse {
        val startTime = System.currentTimeMillis()
        
        val requestBuilder = Request.Builder().url(url)
        
        if (headers.isNotBlank()) {
            headers.lines().forEach { line ->
                val parts = line.split(":", limit = 2)
                if (parts.size == 2) {
                    requestBuilder.addHeader(parts[0].trim(), parts[1].trim())
                }
            }
        }
        
        when (method.uppercase()) {
            "POST", "PUT", "PATCH" -> {
                val mediaType = if (body.trim().startsWith("{") || body.trim().startsWith("[")) {
                    "application/json; charset=utf-8".toMediaType()
                } else {
                    "text/plain; charset=utf-8".toMediaType()
                }
                requestBuilder.method(method, body.toRequestBody(mediaType))
            }
            else -> {
                requestBuilder.method(method, null)
            }
        }
        
        val request = requestBuilder.build()
        val response = httpClient.newCall(request).execute()
        val responseTime = System.currentTimeMillis() - startTime
        
        val responseHeaders = response.headers.toString()
        val responseBody = response.body?.string() ?: ""
        
        val formattedBody = try {
            if (responseBody.trim().startsWith("{") || responseBody.trim().startsWith("[")) {
                gson.toJson(gson.fromJson(responseBody, Any::class.java))
            } else {
                responseBody
            }
        } catch (e: Exception) {
            responseBody
        }
        
        return HttpResponse(
            statusCode = response.code,
            statusMessage = response.message,
            headers = responseHeaders,
            body = formattedBody,
            responseTime = responseTime,
            isSuccess = response.isSuccessful
        )
    }
    
    private suspend fun saveToHistory(url: String, method: String, response: HttpResponse) {
        try {
            repository.addHistory(
                toolName = "HTTP Client",
                input = "$method $url",
                output = "Status: ${response.statusCode} ${response.statusMessage}"
            )
        } catch (e: Exception) {
        }
    }
    
    fun clearAll() {
        url.value = ""
        method.value = "GET"
        headers.value = ""
        requestBody.value = ""
        response.value = null
        errorMessage.value = null
    }
    
    fun setPresetRequest(presetType: String) {
        when (presetType) {
            "GET_JSON" -> {
                method.value = "GET"
                url.value = "https://jsonplaceholder.typicode.com/posts/1"
                headers.value = "Accept: application/json"
                requestBody.value = ""
            }
            "POST_JSON" -> {
                method.value = "POST"
                url.value = "https://jsonplaceholder.typicode.com/posts"
                headers.value = "Content-Type: application/json\nAccept: application/json"
                requestBody.value = "{\n  \"title\": \"Test Post\",\n  \"body\": \"This is a test\",\n  \"userId\": 1\n}"
            }
            "GET_GITHUB" -> {
                method.value = "GET"
                url.value = "https://api.github.com/users/octocat"
                headers.value = "Accept: application/vnd.github.v3+json\nUser-Agent: DevTools-HTTP-Client"
                requestBody.value = ""
            }
        }
    }
    
    data class HttpResponse(
        val statusCode: Int,
        val statusMessage: String,
        val headers: String,
        val body: String,
        val responseTime: Long,
        val isSuccess: Boolean
    )
}