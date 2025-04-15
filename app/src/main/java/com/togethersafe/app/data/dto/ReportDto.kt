package com.togethersafe.app.data.dto

import com.mapbox.geojson.Point
import java.io.File
import java.util.Date

data class ReportReqDto (
    val categoryId: Int,
    val description: String,
    val location: Point,
    val date: String,
    val time: String,
    val media: List<File>,
)

data class ReportPreviewDto(
    val id: String,
    val description: String,
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
    val comments: List<CommentDto>,
    val upvote: Int,
    val downvote: Int,
)

data class ReportUserDto (
    val name: String,
    val profilePhoto: String?,
    val reputation: Int,
)

data class CommentDto (
    val id: Int,
    val comment: String,
    val createdAt: Date,
    val isEdited: Boolean,
    val user: ReportUserDto,
)

data class IncidentDto (
    val id: String,
    val category: String,
)
