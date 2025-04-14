package com.togethersafe.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.dto.ReportPreviewDto
import com.togethersafe.app.data.dto.ReportReqDto
import com.togethersafe.app.data.dto.ReportResDto
import com.togethersafe.app.data.dto.SuccessCreateDto
import com.togethersafe.app.repositories.ReportRepository
import com.togethersafe.app.utils.ApiErrorCallback
import com.togethersafe.app.utils.ApiSuccessCallback
import com.togethersafe.app.utils.getToken
import com.togethersafe.app.utils.handleApiError
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _reportList = MutableStateFlow<List<ReportPreviewDto>>(emptyList())
    private val _report = MutableStateFlow<ReportResDto?>(null)

    val reportList: StateFlow<List<ReportPreviewDto>> get() = _reportList
    val report: StateFlow<ReportResDto?> get() = _report

    fun setReportList(reportList: List<ReportPreviewDto>) {
        _reportList.value = reportList
    }

    fun resetReport() {
        _report.value = null
    }

    fun fetchUserReports(onError: ApiErrorCallback) {
        viewModelScope.launch {
            try {
                withToken(onError) { token ->
                    _reportList.value = repository.getUserReports(token)
                }
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
        }
    }

    fun createReport(
        body: ReportReqDto,
        onSuccess: ApiSuccessCallback<SuccessCreateDto>,
        onError: ApiErrorCallback
    ) {
        viewModelScope.launch {
            try {
                withToken(onError) { token ->
                    onSuccess(repository.createReport(token, body))
                }
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
        }
    }

    fun fetchDetailReport(id: String, onError: ApiErrorCallback, onComplete: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _report.value = repository.getDetailReport(id)
                onSuccess()
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

    private suspend fun withToken(onError: ApiErrorCallback, action: suspend (token: String) -> Unit) {
        val token = getToken(context)

        if (token != null) {
            action("Bearer $token")
        } else {
            onError(401, listOf("token tidak ditemukan"))
        }
    }
}
