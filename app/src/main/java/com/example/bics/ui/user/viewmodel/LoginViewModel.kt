package com.example.bics.ui.user.viewmodel

import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import com.example.bics.data.user.ErrorCode
import kotlinx.coroutines.flow.MutableStateFlow

class LoginViewModel(private val authRepository: AuthRepository): UserFormViewModel() {
    private val _emailUiState = MutableStateFlow(FieldUiState(""))
    private val _passwordUiState = MutableStateFlow(FieldUiState(""))

    val emailUiState = FieldUiStateWrapper(_emailUiState)
    val passwordUiState = FieldUiStateWrapper(_passwordUiState)

    override suspend fun onSubmit(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (validateAllFields()) {
            _available.value = false
            processErrorCode(
                authRepository.login(_emailUiState.value.fieldInput, _passwordUiState.value.fieldInput),
                onSuccess
            )
            _available.value = true
        }
    }

    override fun validateAllFields(): Boolean {
        return listOf(
            validateEmail(_emailUiState),
            validatePasswordField(_passwordUiState)
        ).all { it }
    }

    override fun processFieldErrorCode(errorCode: ErrorCode) {
        processEmailErrorCode(_emailUiState, errorCode)
        processPasswordErrorCode(_passwordUiState, errorCode)
    }

}