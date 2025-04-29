package com.togethersafe.app.data.network

import com.togethersafe.app.data.dto.CommentReqDto
import com.togethersafe.app.data.dto.CommentResDto
import com.togethersafe.app.data.dto.VoteReqDto
import com.togethersafe.app.data.dto.VoteResDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportInteractionService {
    @PATCH("report/{reportId}/vote")
    suspend fun vote(
        @Header("Authorization") token: String,
        @Path("reportId") reportId: String,
        @Body body: VoteReqDto
    ): VoteResDto

    @POST("report/{reportId}/comment")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Path("reportId") reportId: String,
        @Body body: CommentReqDto
    ): CommentResDto

    @PATCH("report/comment/{id}")
    suspend fun updateComment(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: CommentReqDto
    ): CommentResDto

    @DELETE("report/comment/{id}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): CommentResDto
}
