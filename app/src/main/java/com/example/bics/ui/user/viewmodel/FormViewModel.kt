package com.example.bics.ui.user.viewmodel

import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.FieldUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class FormViewModel: ViewModel() {

    protected var _available = MutableStateFlow(true)
    protected var _error = MutableStateFlow(ErrorCode.None)

    val available  = _available.asStateFlow()
    val error = _error.asStateFlow()

    protected fun validateEmail(uiState: MutableStateFlow<FieldUiState>): Boolean {
        uiState.update {
            it.copy(errorCode =
                if (uiState.value.fieldInput.isBlank())
                    ErrorCode.EmptyEmail
                else if (!Patterns.EMAIL_ADDRESS.matcher(uiState.value.fieldInput).matches())
                    ErrorCode.InvalidEmailFormat
                else
                    ErrorCode.None
            )
        }
        return uiState.value.errorCode == ErrorCode.None
    }

    protected fun validateBasicField(
        uiState: MutableStateFlow<FieldUiState>,
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
    protected fun validatePasswordField(passwordUiState: MutableStateFlow<FieldUiState>): Boolean {
        passwordUiState.update {
            it.copy(errorCode =
                when {
                    passwordUiState.value.fieldInput.isBlank() -> ErrorCode.EmptyPassword
                    passwordUiState.value.fieldInput.length < 6 -> ErrorCode.WeakPassword
                    else -> ErrorCode.None
                }
            )
        }
        return passwordUiState.value.errorCode == ErrorCode.None
    }

    protected fun validateConfirmPassword(
        passwordUiState: MutableStateFlow<FieldUiState>,
        confirmPasswordUiState: MutableStateFlow<FieldUiState>,
    ): Boolean {
        confirmPasswordUiState.update {
            it.copy(
                errorCode = if (passwordUiState.value.fieldInput != confirmPasswordUiState.value.fieldInput) {
                    if (passwordUiState.value.errorCode == ErrorCode.None) {
                        passwordUiState.update { state ->
                            state.copy(errorCode = ErrorCode.DifferentPassword)
                        }
                    }
                    ErrorCode.DifferentPassword
                } else
                    ErrorCode.None
            )
        }
        return confirmPasswordUiState.value.errorCode == ErrorCode.None
    }

    protected fun processEmailErrorCode(
        emailUiState: MutableStateFlow<FieldUiState>,
        errorCode: ErrorCode
    ) {
        if (errorCode in listOf(ErrorCode.EmailInUse, ErrorCode.InvalidCredentials, ErrorCode.InvalidEmailFormat, ErrorCode.SameEmail))
            emailUiState.update {
                it.copy(errorCode = errorCode)
            }
    }

    protected fun processPasswordErrorCode(
        passwordUiState: MutableStateFlow<FieldUiState>,
        errorCode: ErrorCode
    ) {
        if (errorCode in listOf(ErrorCode.InvalidCredentials, ErrorCode.WeakPassword))
            passwordUiState.update {
                it.copy(errorCode = errorCode)
            }
    }

    protected fun processErrorCode(errorCode: ErrorCode, onSuccess: () -> Unit = {}) {
        when (errorCode) {
            ErrorCode.None -> onSuccess()
            ErrorCode.Unknown, ErrorCode.NetworkError, ErrorCode.UserDisabled, ErrorCode.UnverifiedEmail -> _error.value = errorCode
            else -> processFieldErrorCode(errorCode)
        }
    }

    abstract suspend fun onSubmit(onSuccess: () -> Unit = {})

    abstract fun validateAllFields(): Boolean

    abstract fun processFieldErrorCode(errorCode: ErrorCode)
}