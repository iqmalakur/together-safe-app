package com.togethersafe.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togethersafe.app.data.dto.ReportPreviewDto
import com.togethersafe.app.data.dto.ReportReqDto
import com.togethersafe.app.data.dto.ReportResDto
import com.togethersafe.app.data.dto.SuccessCreateDto
import com.togethersafe.app.repositories.ReportRepository
import com.togethersafe.app.utils.ApiErrorCallback
import com.togethersafe.app.utils.ApiSuccessCallback
import com.togethersafe.app.utils.handleApiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val repository: ReportRepository) : ViewModel() {
    private val _reportList = MutableStateFlow<List<ReportPreviewDto>>(emptyList())
    private val _report = MutableStateFlow<ReportResDto?>(null)

    val reportList: StateFlow<List<ReportPreviewDto>> get() = _reportList
    val report: StateFlow<ReportResDto?> get() = _report

    fun setReportList(reportList: List<ReportPreviewDto>) {
        _reportList.value = reportList
    }

    fun getUserReports(token: String, onError: ApiErrorCallback) {
        viewModelScope.launch {
            try {
                _reportList.value = repository.getUserReports(token)
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
        }
    }

    fun createReport(
        token: String,
        body: ReportReqDto,
        onSuccess: ApiSuccessCallback<SuccessCreateDto>,
        onError: ApiErrorCallback
    ) {
        viewModelScope.launch {
            try {
                onSuccess(repository.createReport(token, body))
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
        }
    }

    fun getDetailReport(token: String, id: String, onError: ApiErrorCallback) {
        viewModelScope.launch {
            try {
                _report.value = repository.getDetailReport(token, id)
            } catch (e: Exception) {
                handleApiError(this::class, e, onError)
            }
        }
    }
}