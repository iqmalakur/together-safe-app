package com.togethersafe.app.data.dto

import com.mapbox.geojson.Point
import java.io.File

data class ReportReqDto(
    val categoryId: Int,
    val description: String,
    val location: Point,
    val date: String,
    val time: String,
    val media: List<File>,
)

data class ReportItemDto(
    val id: String,
    val description: String,
    val category: String,
    val location: String,
    val date: String,
    val time: String,
    val status: String,
)

data class ReportResDto(
    val id: String,
    val incident: IncidentDto,
    val user: ReportUserDto,
    val description: String,
    val date: String,
    val time: String,
    val status: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val attachments: List<String>,
    val comments: List<CommentResDto>,
    val upvote: Int,
    val downvote: Int,
)

data class IncidentDto(
    val id: String,
    val category: String,
)
