package com.example.bics.data.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FieldUiState (
    val fieldInput: String = "",
    val errorCode: ErrorCode = ErrorCode.None,
)

data class FieldUiStateWrapper(
    private val _uiState: MutableStateFlow<FieldUiState>,
    private val onFieldValueChanged: (String, MutableStateFlow<FieldUiState>) -> Unit =
        { newValue, stateFlow ->
            stateFlow.update {
                it.copy(fieldInput = newValue, errorCode = ErrorCode.None)
            }
        }
) {
    val uiState = _uiState.asStateFlow()
    fun value() = _uiState.value.fieldInput
    fun onValueChanged(newValue: String) = onFieldValueChanged(newValue, _uiState)
}
