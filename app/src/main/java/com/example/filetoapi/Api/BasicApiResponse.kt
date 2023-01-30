package com.example.filetoapi.Api

data class BasicApiResponse<T>(
    val message: String? = null,
    val successful: Boolean,
    val data: T? = null
)