package com.example.bics.ui.user.viewmodel

import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import kotlinx.coroutines.flow.MutableStateFlow

class ChangePasswordViewModel(private val authRepository: AuthRepository): FormViewModel() {
    private val _passwordUiState = MutableStateFlow(FieldUiState())
    private val _confirmPasswordUiState = MutableStateFlow(FieldUiState())

    val passwordUiState = FieldUiStateWrapper(_passwordUiState)
    val confirmPasswordUiState = FieldUiStateWrapper(_confirmPasswordUiState)

    override suspend fun onSubmit(onSuccess: () -> Unit) {
        if (validateAllFields()) {
            _available.value = false
            if (authRepository.refresh()) processErrorCode(authRepository.changePassword(_passwordUiState.value.fieldInput), onSuccess)
            _available.value = true
        }
    }

    override fun validateAllFields(): Boolean {
        return listOf(
            validatePasswordField(_passwordUiState),
            validateConfirmPassword(_passwordUiState, _confirmPasswordUiState)
            ).all { it }
    }

    override fun processFieldErrorCode(errorCode: ErrorCode) {
        processPasswordErrorCode(_passwordUiState, errorCode)
    }
}