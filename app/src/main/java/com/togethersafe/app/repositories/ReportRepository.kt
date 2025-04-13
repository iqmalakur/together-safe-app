package com.togethersafe.app.repositories

import com.togethersafe.app.data.dto.ReportPreviewDto
import com.togethersafe.app.data.dto.ReportReqDto
import com.togethersafe.app.data.dto.ReportResDto
import com.togethersafe.app.data.dto.SuccessCreateDto
import com.togethersafe.app.data.network.ReportService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(private val service: ReportService) {
    suspend fun getUserReports(token: String): List<ReportPreviewDto> {
        return service.fetchUserReport(token)
    }

    suspend fun createReport(token: String, body: ReportReqDto): SuccessCreateDto {
        val textPlain = "text/plain".toMediaType()

        val categoryId = body.categoryId.toString().toRequestBody(textPlain)
        val description = body.description.toRequestBody(textPlain)

        val locationString = "${body.location.latitude()},${body.location.longitude()}"
        val location = locationString.toRequestBody(textPlain)

        val date = body.date.toRequestBody(textPlain)
        val time = body.time.toRequestBody(textPlain)

        val mediaPart = body.media.map { file ->
            val requestFile = file.asRequestBody("image/*".toMediaType())
            MultipartBody.Part.createFormData("media", file.name, requestFile)
        }

        return service.createReport(
            token = token,
            categoryId = categoryId,
            description = description,
            location = location,
            date = date,
            time = time,
            media = mediaPart
        )
    }

    suspend fun getDetailReport(token: String, id: String): ReportResDto {
        return service.fetchDetailReport(token, id)
    }
}