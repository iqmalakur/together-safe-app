package com.togethersafe.app.data.dto

data class IncidentResDto(
    val id: String,
    val category: String,
    val riskLevel: String,
    val location: String,
    val date: String,
    val time: String,
    val status: String,
    val radius: Double,
    val latitude: Double,
    val longitude: Double,
)

data class IncidentDetailResDto(
    val id: String,
    val category: String,
    val riskLevel: String,
    val location: String,
    val date: String,
    val time: String,
    val status: String,
    val upvoteCount: Int,
    val downvoteCount: Int,
    val mediaUrls: List<String>,
    val reports: List<ReportItemDto>,
)

data class CategoryResDto(
    val id: Int,
    val name: String,
)
