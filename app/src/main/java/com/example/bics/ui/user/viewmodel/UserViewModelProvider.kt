package com.example.bics.ui.user.viewmodel

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bics.bicsApplication

object UserViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            SignupViewModel(this.bicsApplication().container.authRepository)
        }

        initializer {
            LoginViewModel(this.bicsApplication().container.authRepository)
        }

        initializer {
            ForgotPasswordViewModel(this.bicsApplication().container.authRepository)
        }

        initializer {
            val container = this.bicsApplication().container

            EditProfileViewModel(container.authRepository, container.profileRepository)
        }

        initializer {
            ConfirmPasswordViewModel(this.bicsApplication().container.authRepository)
        }

        initializer {
            ChangePasswordViewModel(this.bicsApplication().container.authRepository)
        }

        initializer {
            ChangeEmailViewModel(this.bicsApplication().container.authRepository)
        }

        initializer {
            val container = this.bicsApplication().container
            AuthViewModel(container.authRepository, container.profileRepository)
        }

    }
}