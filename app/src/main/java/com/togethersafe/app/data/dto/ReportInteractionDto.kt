package com.togethersafe.app.data.dto

import java.util.Date

data class VoteReqDto(
    val type: String?,
)

data class VoteResDto(
    val userEmail: String,
    val reportId: String,
    val type: String?,
)

data class CommentReqDto(val comment: String)

data class ReportUserDto(
    val email: String,
    val name: String,
    val profilePhoto: String?,
)

data class CommentResDto(
    val id: Int,
    val comment: String,
    val createdAt: Date,
    val isEdited: Boolean,
    val user: ReportUserDto,
)
