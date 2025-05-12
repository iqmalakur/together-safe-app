package com.togethersafe.app.data.network

import com.togethersafe.app.data.dto.ReportItemDto
import com.togethersafe.app.data.dto.ReportResDto
import com.togethersafe.app.data.dto.SuccessCreateDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ReportService {
    @GET("report")
    suspend fun fetchUserReport(@Header("Authorization") token: String): List<ReportItemDto>

    @Multipart
    @POST("report")
    suspend fun createReport(
        @Header("Authorization") token: String,
        @Part("categoryId") categoryId: RequestBody,
        @Part("isAnonymous") isAnonymous: RequestBody,
        @Part("description") description: RequestBody,
        @Part("location") location: RequestBody,
        @Part("date") date: RequestBody,
        @Part("time") time: RequestBody,
        @Part media: List<MultipartBody.Part>,
    ): SuccessCreateDto

    @GET("report/{id}")
    suspend fun fetchDetailReport(@Path("id") id: String): ReportResDto
}
