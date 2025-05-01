package com.togethersafe.app.repositories

import com.togethersafe.app.data.dto.CommentReqDto
import com.togethersafe.app.data.dto.CommentResDto
import com.togethersafe.app.data.dto.VoteReqDto
import com.togethersafe.app.data.dto.VoteResDto
import com.togethersafe.app.data.network.ReportInteractionService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportInteractionRepository @Inject constructor(private val service: ReportInteractionService) {
    suspend fun findUserVote(token: String, reportId: String): VoteResDto {
        return service.findUserVote(token, reportId)
    }

    suspend fun vote(token: String, reportId: String, voteType: String?): VoteResDto {
        return service.vote(token, reportId, VoteReqDto(voteType))
    }

    suspend fun createComment(token: String, reportId: String, comment: String): CommentResDto {
        return service.createComment(token, reportId, CommentReqDto(comment))
    }

    suspend fun updateComment(token: String, id: Int, comment: String): CommentResDto {
        return service.updateComment(token, id, CommentReqDto(comment))
    }

    suspend fun deleteComment(token: String, id: Int): CommentResDto {
        return service.deleteComment(token, id)
    }
}
