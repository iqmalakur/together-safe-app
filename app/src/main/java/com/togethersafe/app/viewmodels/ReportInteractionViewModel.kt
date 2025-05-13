package com.togethersafe.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.dto.CommentResDto
import com.togethersafe.app.data.dto.VoteResDto
import com.togethersafe.app.repositories.ReportInteractionRepository
import com.togethersafe.app.utils.ApiErrorCallback
import com.togethersafe.app.utils.ApiSuccessCallback
import com.togethersafe.app.utils.handleApiError
import com.togethersafe.app.utils.withToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportInteractionViewModel @Inject constructor(
    private val repository: ReportInteractionRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    fun findUserVote(
        reportId: String,
        onError: ApiErrorCallback,
        onSuccess: (voteType: String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                withToken(context, onError) { token ->
                    val result = repository.findUserVote(token, reportId)
                    onSuccess(result.type)
                }
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
        }
    }

    fun vote(
        reportId: String,
        prevVoteType: String?,
        targetVoteType: String?,
        onError: ApiErrorCallback,
        onSuccess: (voteType: VoteResDto) -> Unit,
        onComplete: () -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val voteType = when {
                    prevVoteType == targetVoteType -> null
                    else -> targetVoteType
                }

                withToken(context, onError) { token ->
                    val result = repository.vote(token, reportId, prevVoteType, voteType)
                    onSuccess(result)
                }
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

    fun createComment(
        reportId: String,
        comment: String,
        onError: ApiErrorCallback,
        onSuccess: suspend (comment: CommentResDto) -> Unit,
        onComplete: () -> Unit,
    ) {
        viewModelScope.launch {
            try {
                withToken(context, onError) { token ->
                    onSuccess(repository.createComment(token, reportId, comment))
                }
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

    fun editComment(
        id: Int,
        comment: String,
        onError: ApiErrorCallback,
        onSuccess: ApiSuccessCallback<CommentResDto>,
        onComplete: () -> Unit,
    ) {
        viewModelScope.launch {
            try {
                withToken(context, onError) { token ->
                    onSuccess(repository.updateComment(token, id, comment))
                }
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

    fun deleteComment(
        id: Int,
        onError: ApiErrorCallback,
        onSuccess: (id: Int) -> Unit,
        onComplete: () -> Unit,
    ) {
        viewModelScope.launch {
            try {
                withToken(context, onError) { token ->
                    val result = repository.deleteComment(token, id)
                    onSuccess(result.id)
                }
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

}