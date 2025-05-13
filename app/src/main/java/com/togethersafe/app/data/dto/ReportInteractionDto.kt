package com.togethersafe.app.data.dto

import java.util.Date

data class VoteReqDto(
    val prevVoteType: String?,
    val newVoteType: String?,
)

data class UserVoteResDto(
    val userEmail: String,
    val reportId: String,
    val type: String?,
)

data class VoteResDto(
    val userEmail: String,
    val reportId: String,
    val type: String?,
    val reporterReputation: Int,
)

data class CommentReqDto(val comment: String)

data class ReportUserDto (
    val email: String,
    val name: String,
    val profilePhoto: String?,
    val reputation: Int,
)

data class CommentResDto (
    val id: Int,
    val comment: String,
    val createdAt: Date,
    val isEdited: Boolean,
    val user: ReportUserDto,
)
