package com.example.bics.ui.user.viewmodel

import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ChangeEmailViewModel(private val authRepository: AuthRepository): UserFormViewModel() {
    private val _email = MutableStateFlow(FieldUiState(""))

    val email = FieldUiStateWrapper(_email)
    override suspend fun onSubmit(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (validateAllFields()) {
            _available.value = false
            if (authRepository.refresh()) {
                processErrorCode(authRepository.changeEmail(_email.value.fieldInput), onSuccess)
            } else _error.update { ErrorCode.EmailChanged }
            _available.value = true
        }
    }

    override fun validateAllFields(): Boolean {
        return validateEmail(_email)
    }

    override fun processFieldErrorCode(errorCode: ErrorCode) {
        processEmailErrorCode(_email, errorCode)
    }
}