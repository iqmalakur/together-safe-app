package com.togethersafe.app.data.dto

data class SuccessCreateDto(
    val id: String,
    val message: String
)

data class ApiErrorDto(val message: String)
data class ApiBadRequestDto(val messages: List<String>)
