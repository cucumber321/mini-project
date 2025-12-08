package com.example.bics.ui.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.FieldUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

abstract class FormViewModel(): ViewModel() {
    protected var _available = MutableStateFlow(true)
    protected var _error = MutableStateFlow(ErrorCode.None)

    val available  = _available.asStateFlow()
    val error = _error.asStateFlow()

    init {
        _error.onEach {
            if (it != ErrorCode.None) {
                delay(1000)
                _error.update { ErrorCode.None }
            }
        }.launchIn(viewModelScope)
    }

    protected fun validateBasicField(
        uiState: MutableStateFlow<FieldUiState<String>>,
        errorCode: ErrorCode,
    ): Boolean {
        uiState.update {
            it.copy(errorCode =
                if (uiState.value.fieldInput.isBlank())
                    errorCode
                else
                    ErrorCode.None
            )
        }
        return uiState.value.errorCode == ErrorCode.None
    }

    protected fun processErrorCode(errorCode: ErrorCode, onSuccess: () -> Unit = {}) {
        when (errorCode) {
            ErrorCode.None -> onSuccess()
            ErrorCode.Unknown, ErrorCode.NetworkError, ErrorCode.UserDisabled, ErrorCode.UnverifiedEmail -> _error.value = errorCode
            else -> processFieldErrorCode(errorCode)
        }
    }

    abstract suspend fun onSubmit(onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {})

    abstract fun validateAllFields(): Boolean
    abstract fun processFieldErrorCode(errorCode: ErrorCode)
}