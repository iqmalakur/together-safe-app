package com.togethersafe.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.dto.ReportItemDto
import com.togethersafe.app.data.dto.ReportReqDto
import com.togethersafe.app.data.dto.ReportResDto
import com.togethersafe.app.repositories.ReportRepository
import com.togethersafe.app.utils.ApiErrorCallback
import com.togethersafe.app.utils.handleApiError
import com.togethersafe.app.utils.withToken
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
    private val _reportList = MutableStateFlow<List<ReportItemDto>>(emptyList())
    private val _report = MutableStateFlow<ReportResDto?>(null)

    val reportList: StateFlow<List<ReportItemDto>> get() = _reportList
    val report: StateFlow<ReportResDto?> get() = _report

    fun setReportList(reportList: List<ReportItemDto>) {
        _reportList.value = reportList
    }

    fun resetReport() {
        _report.value = null
    }

    fun fetchUserReports(onError: ApiErrorCallback, onComplete: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                withToken(context, onError) { token ->
                    _reportList.value = repository.getUserReports(token)
                }
                onSuccess()
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

    fun createReport(
        body: ReportReqDto,
        onError: ApiErrorCallback,
        onComplete: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            try {
                withToken(context, onError) { token ->
                    repository.createReport(token, body)
                    onSuccess()
                }
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
            onComplete()
        }
    }

    fun fetchDetailReport(
        id: String,
        onError: ApiErrorCallback,
        onComplete: () -> Unit,
        onSuccess: () -> Unit
    ) {
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
}
