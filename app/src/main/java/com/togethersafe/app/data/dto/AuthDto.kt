package com.togethersafe.app.data.dto

data class AuthResDto(
    val email: String,
    val name: String,
    val token: String,
    val profilePhoto: String?,
)

data class LoginReqDto(
    val email: String,
    val password: String,
)

data class ValidateTokenReqDto(val token: String)
