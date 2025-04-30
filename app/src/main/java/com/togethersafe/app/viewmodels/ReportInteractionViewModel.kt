package com.togethersafe.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.repositories.ReportInteractionRepository
import com.togethersafe.app.utils.ApiErrorCallback
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
        onSuccess: (voteType: String?) -> Unit,
        onComplete: () -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val voteType = when {
                    prevVoteType == targetVoteType -> null
                    else -> targetVoteType
                }

                withToken(context, onError) { token ->
                    val result = repository.vote(token, reportId, voteType)
                    onSuccess(result.type)
                }
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

}