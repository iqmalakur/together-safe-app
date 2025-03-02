package com.togethersafe.app.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.togethersafe.app.repository.IncidentRepository
import com.togethersafe.app.ui.viewmodel.IncidentViewModel

class ViewModelFactory<T: ViewModel>(
    private val viewModelClass: Class<T>,
    private val creator: () -> T,
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(viewModelClass)) {
            @Suppress("UNCHECKED_CAST")
            return creator() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
