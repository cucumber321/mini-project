package com.example.bics.ui.user.viewmodel

import android.util.Patterns
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.FieldUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

abstract class UserFormViewModel: FormViewModel() {
    protected fun validateEmail(uiState: MutableStateFlow<FieldUiState<String>>): Boolean {
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

    protected fun validatePasswordField(passwordUiState: MutableStateFlow<FieldUiState<String>>): Boolean {
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
        passwordUiState: MutableStateFlow<FieldUiState<String>>,
        confirmPasswordUiState: MutableStateFlow<FieldUiState<String>>,
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
        emailUiState: MutableStateFlow<FieldUiState<String>>,
        errorCode: ErrorCode
    ) {
        if (errorCode in listOf(ErrorCode.EmailInUse, ErrorCode.InvalidCredentials, ErrorCode.InvalidEmailFormat, ErrorCode.SameEmail))
            emailUiState.update {
                it.copy(errorCode = errorCode)
            }
    }

    protected fun processPasswordErrorCode(
        passwordUiState: MutableStateFlow<FieldUiState<String>>,
        errorCode: ErrorCode
    ) {
        if (errorCode in listOf(ErrorCode.InvalidCredentials, ErrorCode.WeakPassword))
            passwordUiState.update {
                it.copy(errorCode = errorCode)
            }
    }
}
