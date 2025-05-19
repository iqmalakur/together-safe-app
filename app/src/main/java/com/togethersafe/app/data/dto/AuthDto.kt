package com.togethersafe.app.data.dto

import java.io.File

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

data class RegisterReqDto(
    val name: String,
    val email: String,
    val password: String,
    val profilePhoto: File?,
)

data class ValidateTokenReqDto(val token: String)
