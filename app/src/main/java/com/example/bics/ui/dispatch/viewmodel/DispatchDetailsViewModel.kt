package com.example.bics.ui.dispatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.dispatch.Dispatch
import com.example.bics.data.dispatch.DispatchRepository
import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.repository.profile.ProfileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.format

class DispatchDetailsViewModel(private val dispatchRepository: DispatchRepository, private val profileRepository: ProfileRepository, private val authRepository: AuthRepository): ViewModel() {
    private val _dispatch = MutableStateFlow(Dispatch())
    private val _isLoading = MutableStateFlow(false)
    private val _enabled = MutableStateFlow(true)
    private val _createdBy = MutableStateFlow("")
    private val _lastModifiedBy = MutableStateFlow("")

    val dispatch = _dispatch.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    val enabled = _enabled.asStateFlow()
    val createdBy = _createdBy.asStateFlow()
    val lastModifiedBy = _lastModifiedBy.asStateFlow()

    init {
        _dispatch.onEach { d ->
            if (d.createdBy.isNotBlank()) _createdBy.update { format("%s (%s)", profileRepository.getUser(d.createdBy).username, d.createdBy) }
            (d.lastModifiedBy ?: d.createdBy).let { userId ->
                if (userId.isNotBlank()) _lastModifiedBy.update {
                    format(
                        "%s (%s)",
                        profileRepository.getUser(userId).username,
                        userId
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadDetails(dispatchID: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            _dispatch.update{ dispatchRepository.getDispatch(dispatchID) }
            delay(100)
            _isLoading.update { false }
        }
    }

    fun setChosenDispatchId() {
        dispatchRepository.state.last()["dispatch_id"] = _dispatch.value.id
    }

    fun deleteDispatch(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _enabled.update { false }
            if (authRepository.refresh()) {
                dispatchRepository.deleteDispatch(dispatch.value.id)
                onSuccess()
            }
            delay(100)
            _enabled.update { true }
        }
    }

}