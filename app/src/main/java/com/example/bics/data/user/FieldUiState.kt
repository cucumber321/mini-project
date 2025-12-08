package com.example.bics.data.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FieldUiState<T> (
    val fieldInput: T,
    val errorCode: ErrorCode = ErrorCode.None,
)

data class FieldUiStateWrapper<T>(
    private val _uiState: MutableStateFlow<FieldUiState<T>>,
    private val onFieldValueChanged: (T, MutableStateFlow<FieldUiState<T>>) -> Unit =
        { newValue, stateFlow ->
            stateFlow.update {
                FieldUiState(fieldInput = newValue, errorCode = ErrorCode.None)
            }
        }
) {
    val uiState = _uiState.asStateFlow()
    fun value() = _uiState.value.fieldInput
    fun onValueChanged(newValue: T) = onFieldValueChanged(newValue, _uiState)
}
