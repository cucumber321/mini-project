package com.example.bics.ui.user.viewmodel

import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import com.example.bics.data.user.ErrorCode
import kotlinx.coroutines.flow.MutableStateFlow

class SignupViewModel(private val authRepository: AuthRepository): FormViewModel() {
    private val _usernameUiState = MutableStateFlow(FieldUiState())
    private val _emailUiState = MutableStateFlow(FieldUiState())
    private val _passwordUiState = MutableStateFlow(FieldUiState())
    private val _confirmPasswordUiState = MutableStateFlow(FieldUiState())

    val usernameUiState = FieldUiStateWrapper(_usernameUiState)
    val emailUiState = FieldUiStateWrapper(_emailUiState)
    val passwordUiState = FieldUiStateWrapper(_passwordUiState)
    val confirmPasswordUiState = FieldUiStateWrapper(_confirmPasswordUiState)

    override suspend fun onSubmit(onSuccess: () -> Unit) {
        if (validateAllFields()) {
            _available.value = false
            processErrorCode(authRepository.signup(
                username = _usernameUiState.value.fieldInput,
                email = _emailUiState.value.fieldInput,
                password = _passwordUiState.value.fieldInput
            ), onSuccess)
            _available.value = true
        }
    }
    override fun validateAllFields(): Boolean {
        return listOf(
            validateBasicField(_usernameUiState, ErrorCode.EmptyUsername),
            validateEmail(_emailUiState),
            validatePasswordField(_passwordUiState),
            validateConfirmPassword(_passwordUiState, _confirmPasswordUiState)
        ).all { it }
    }

    override fun processFieldErrorCode(errorCode: ErrorCode) {
        processEmailErrorCode(_emailUiState, errorCode)
        processPasswordErrorCode(_passwordUiState, errorCode)
    }
}