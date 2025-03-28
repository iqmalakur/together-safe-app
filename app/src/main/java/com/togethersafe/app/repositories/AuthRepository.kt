package com.togethersafe.app.repositories

import com.togethersafe.app.data.dto.AuthResDto
import com.togethersafe.app.data.dto.LoginReqDto
import com.togethersafe.app.data.dto.RegisterReqDto
import com.togethersafe.app.data.dto.ValidateTokenReqDto
import com.togethersafe.app.data.network.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    suspend fun register(registerDto: RegisterReqDto): AuthResDto {
        val textPlain = "text/plain".toMediaType()

        val name = registerDto.name.toRequestBody(textPlain)
        val email = registerDto.email.toRequestBody(textPlain)
        val phone = registerDto.phone.toRequestBody(textPlain)
        val password = registerDto.password.toRequestBody(textPlain)

        var profilePhoto: MultipartBody.Part? = null
        val file = registerDto.profilePhoto

        if (file != null) {
            val requestFile = file.asRequestBody("image/jpg".toMediaType())
            profilePhoto = MultipartBody.Part.createFormData("profilePhoto", file.name, requestFile)
        }

        return apiService.register(
            name = name,
            email = email,
            phone = phone,
            password = password,
            profilePhoto = profilePhoto,
        )
    }
}