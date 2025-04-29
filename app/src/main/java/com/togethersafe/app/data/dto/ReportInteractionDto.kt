package com.togethersafe.app.data.dto

data class VoteReqDto(val voteType: String?)

data class VoteResDto(
    val userEmail: String,
    val reportId: String,
    val voteType: String?,
)

data class CommentReqDto(val comment: String)

data class CommentResDto(
    val id: Int,
    val userEmail: String,
    val reportId: String,
    val comment: String,
)