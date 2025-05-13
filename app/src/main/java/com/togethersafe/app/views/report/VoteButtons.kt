package com.togethersafe.app.views.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.togethersafe.app.data.dto.ReportResDto
import com.togethersafe.app.data.dto.VoteResDto
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.ReportInteractionViewModel

@Composable
fun VoteButtons(
    report: ReportResDto,
    isLoggedIn: Boolean,
    isUserOwnReport: Boolean,
    onReputationChange: (Int) -> Unit
) {
    val appViewModel: AppViewModel = getViewModel()
    val reportInteractionViewModel: ReportInteractionViewModel = getViewModel()

    var upvoteCount by remember { mutableIntStateOf(0) }
    var downvoteCount by remember { mutableIntStateOf(0) }
    var currentVote by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(report) {
        upvoteCount = report.upvote
        downvoteCount = report.downvote
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            reportInteractionViewModel.findUserVote(
                report.id,
                onError = { _, messages -> appViewModel.setToastMessage(messages[0]) },
            ) { currentVote = it }
        }
    }

    VoteButton(
        type = "upvote",
        text = "👍 $upvoteCount",
        reportId = report.id,
        currentVote = currentVote,
        enabled = isLoggedIn && !isUserOwnReport,
    ) {
        when (currentVote) {
            null -> upvoteCount++
            "upvote" -> upvoteCount--
            else -> {
                upvoteCount++
                downvoteCount--
            }
        }

        currentVote = it.type
        onReputationChange(it.reporterReputation)
    }

    VoteButton(
        type = "downvote",
        text = "👎 $downvoteCount",
        reportId = report.id,
        currentVote = currentVote,
        enabled = isLoggedIn && !isUserOwnReport,
    ) {
        when (currentVote) {
            null -> downvoteCount++
            "downvote" -> downvoteCount--
            else -> {
                downvoteCount++
                upvoteCount--
            }
        }

        currentVote = it.type
        onReputationChange(it.reporterReputation)
    }
}

@Composable
private fun VoteButton(
    type: String,
    text: String,
    reportId: String,
    currentVote: String?,
    enabled: Boolean,
    onSuccessVote: (newVote: VoteResDto) -> Unit
) {
    val appViewModel: AppViewModel = getViewModel()
    val reportInteractionViewModel: ReportInteractionViewModel = getViewModel()

    TextButton(
        colors = ButtonDefaults.textButtonColors(
            containerColor = when (currentVote) {
                type -> Color.Gray
                else -> Color.White
            },
            contentColor = when (currentVote) {
                type -> Color.White
                else -> Color.Black
            },
        ),
        border = BorderStroke(1.dp, Color.Black.copy(0.2f)),
        enabled = enabled,
        onClick = {
            appViewModel.setLoading(true)
            reportInteractionViewModel.vote(
                reportId = reportId,
                prevVoteType = currentVote,
                targetVoteType = type,
                onError = { _, messages -> appViewModel.setToastMessage(messages[0]) },
                onSuccess = onSuccessVote
            ) { appViewModel.setLoading(false) }
        }
    ) {
        Text(text)
    }
}
