package com.togethersafe.app.data.network

import com.togethersafe.app.data.dto.AuthResDto
import com.togethersafe.app.data.dto.LoginReqDto
import com.togethersafe.app.data.dto.SuccessCreateDto
import com.togethersafe.app.data.dto.ValidateTokenReqDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginReqDto): AuthResDto

    @Multipart
    @POST("auth/register")
    suspend fun register(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part profilePhoto: MultipartBody.Part?,
    ): SuccessCreateDto

    @POST("auth/validate-token")
    suspend fun validateToken(@Body body: ValidateTokenReqDto): AuthResDto
}
