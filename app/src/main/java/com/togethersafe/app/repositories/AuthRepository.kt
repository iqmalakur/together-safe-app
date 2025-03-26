package com.togethersafe.app.repositories

import com.togethersafe.app.data.dto.AuthResDto
import com.togethersafe.app.data.dto.LoginReqDto
import com.togethersafe.app.data.dto.ValidateTokenReqDto
import com.togethersafe.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun validateToken(token: String): AuthResDto {
        val body = ValidateTokenReqDto(token)
        return apiService.validateToken(body)
    }

    suspend fun login(email: String, password: String): AuthResDto {
        val body = LoginReqDto(email, password)
        return apiService.login(body)
    }
}