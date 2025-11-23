package com.example.bics.ui.user.viewmodel

import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ForgotPasswordViewModel(private val authRepository: AuthRepository): FormViewModel() {
    private val _emailUiState = MutableStateFlow(FieldUiState())
    private val isLoggedIn = authRepository.isLoggedIn()

    val user = authRepository.getUserStream().value
    val emailUiState = FieldUiStateWrapper(_emailUiState)


    init {
        if (user != null) _emailUiState.update {
            it.copy(fieldInput = user.email?:"")
        }
    }

    override suspend fun onSubmit(onSuccess: () -> Unit) {
        if (validateAllFields()) {
            _available.value = false
            if (!isLoggedIn || authRepository.refresh()) {
                processErrorCode(
                    authRepository.forgotPassword(_emailUiState.value.fieldInput),
                    onSuccess
                )
            }
            _available.value = true
        }
    }

    override fun validateAllFields(): Boolean {
        return validateEmail(_emailUiState)
    }

    override fun processFieldErrorCode(errorCode: ErrorCode) {
        processEmailErrorCode(_emailUiState, errorCode)
    }
}