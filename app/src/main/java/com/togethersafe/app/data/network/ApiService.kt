package com.togethersafe.app.data.network

import com.togethersafe.app.data.dto.AuthResDto
import com.togethersafe.app.data.dto.LoginReqDto
import com.togethersafe.app.data.dto.ValidateTokenReqDto
import com.togethersafe.app.data.model.Incident
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("incident")
    suspend fun fetchIncidents(): List<Incident>

    @POST("auth/login")
    suspend fun login(@Body body: LoginReqDto): AuthResDto

    @POST("auth/validate_token")
    suspend fun validateToken(@Body body: ValidateTokenReqDto): AuthResDto
}
