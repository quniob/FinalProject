package com.example.finalproject

data class HttpRequest(
    val method: String = "GET",
    val url: String = "",
    val headers: Map<String, String> = emptyMap(),
    val body: String = ""
)

data class HttpResponse(
    val statusCode: Int,
    val statusMessage: String,
    val headers: Map<String, String>,
    val body: String,
    val responseTime: Long,
    val isSuccess: Boolean = statusCode in 200..299
)