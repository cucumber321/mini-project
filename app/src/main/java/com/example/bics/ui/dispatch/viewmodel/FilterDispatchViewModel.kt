package com.example.bics.ui.dispatch.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bics.data.dispatch.DispatchFilterSettings
import com.example.bics.data.dispatch.DispatchRepository
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FilterDispatchViewModel(dispatchRepository: DispatchRepository): ViewModel() {
    val filterSettings = dispatchRepository.state.last().getMutableStateFlow(
        "filter",
        DispatchFilterSettings()
    )
    private val _startDate = MutableStateFlow(FieldUiState(filterSettings.value.startDateSeconds))
    private val _endDate = MutableStateFlow(FieldUiState(filterSettings.value.endDateSeconds))

    val startDate = FieldUiStateWrapper(_startDate)
    val endDate = FieldUiStateWrapper(_endDate)

    fun onSubmit(onSuccess: () -> Unit) {
        val start = startDate.value()
        val end = endDate.value()

        if (start != null && end != null) {
            if (start > end) {
                _startDate.update { it.copy(errorCode = ErrorCode.InvalidTimeRange) }
                _endDate.update { it.copy(errorCode = ErrorCode.InvalidTimeRange) }
                return
            }
        }

        filterSettings.update {
            DispatchFilterSettings(
                startDate.value(),
                endDate.value()
            )
        }

        onSuccess()
    }

    fun reset() {
        startDate.onValueChanged(null)
        endDate.onValueChanged(null)
    }
}