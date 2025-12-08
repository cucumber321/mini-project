package com.example.bics.ui.user.viewmodel

import android.net.Uri
import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import com.example.bics.data.user.FirestoreUserField
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditProfileViewModel(private val authRepository: AuthRepository, private val profileRepository: ProfileRepository): UserFormViewModel() {
    private val _usernameUiState = MutableStateFlow(FieldUiState(""))
    private var _selectedImageUri: MutableStateFlow<Uri>

    private val profile = profileRepository.getUserStream()

    val selectedImageUri: StateFlow<Uri>
    val usernameUiState = FieldUiStateWrapper(_usernameUiState)

    init {
        _usernameUiState.update {
            it.copy(fieldInput = profile.value.username)
        }
        _selectedImageUri = MutableStateFlow(profile.value.profilePictureUri)
        selectedImageUri = _selectedImageUri.asStateFlow()
    }

    fun onImageChanged(uri: Uri?) {
        if (uri != null) {
            _selectedImageUri.value = uri
        }
    }

    override suspend fun onSubmit(onSuccess: () -> Unit, onFailure: (String) -> Unit) = coroutineScope {
        if (validateAllFields()) {
            _available.value = false
            if (authRepository.refresh()) {
                async {
                    profileRepository.updateProfilePicture(selectedImageUri.value)
                }
                async {
                    profileRepository.updateUser(FirestoreUserField.Username, _usernameUiState.value.fieldInput)
                }
                onSuccess()
            }
            _available.value = true
        }
    }

    override fun validateAllFields(): Boolean {
        return validateBasicField(_usernameUiState, ErrorCode.EmptyUsername)
    }

    override fun processFieldErrorCode(errorCode: ErrorCode) {

    }
}
