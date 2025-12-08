package com.example.bics.ui.schedule.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleRepository
import com.example.bics.data.schedule.ScheduleStateKeys
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import com.example.bics.data.user.UserProfile
import com.example.bics.ui.user.viewmodel.FormViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

abstract class ScheduleFormViewModel(private val scheduleRepository: ScheduleRepository, private val profileRepository: ProfileRepository, private val authRepository: AuthRepository): FormViewModel() {
    protected val title = MutableStateFlow(FieldUiState(""))
    protected val description = MutableStateFlow(FieldUiState(""))
    protected var date = MutableStateFlow(FieldUiState<Instant?>(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
    protected var startTime = MutableStateFlow(FieldUiState<Long>(0))
    protected var endTime = MutableStateFlow(FieldUiState<Long>(0))
    protected val uids = scheduleRepository.state.last().getMutableStateFlow(ScheduleStateKeys.UsersAssigned.key, emptyList<String>())
    protected val users = MutableStateFlow(FieldUiState(emptyList<UserProfile>()))
    protected val _loading = MutableStateFlow(false)


    val titleUiState = FieldUiStateWrapper(title)
    val descriptionUiState = FieldUiStateWrapper(description)
    val dateUiState = FieldUiStateWrapper(date)
    val startTimeUiState = FieldUiStateWrapper(startTime)
    val endTimeUiState = FieldUiStateWrapper(endTime)
    val usersUiState = FieldUiStateWrapper(users)
    val loading = _loading.asStateFlow()

    init {
        uids.onEach { uids ->
            usersUiState.onValueChanged(profileRepository.getUsers(uids))
            users.update { it.copy(errorCode = ErrorCode.None) }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        scheduleRepository.state.removeAt(scheduleRepository.state.lastIndex)
    }
    protected fun validateUserList(
        users: MutableStateFlow<FieldUiState<List<UserProfile>>>
    ): Boolean {
        users.update {
            it.copy(
                errorCode = if (it.fieldInput.isEmpty())
                    ErrorCode.NoUsers
                else
                    ErrorCode.None
            )
        }
        return users.value.errorCode == ErrorCode.None
    }
    protected fun validateTimeRangeField(
        startTime: MutableStateFlow<FieldUiState<Long>>,
        endTime: MutableStateFlow<FieldUiState<Long>>
    ): Boolean {
        return if (startTime.value.fieldInput > endTime.value.fieldInput) {
            startTime.update {
                it.copy(errorCode = ErrorCode.InvalidTimeRange)
            }
            endTime.update {
                it.copy(errorCode = ErrorCode.InvalidTimeRange)
            }
            false
        } else {
            startTime.update {
                it.copy(errorCode = ErrorCode.None)
            }
            endTime.update {
                it.copy(errorCode = ErrorCode.None)
            }
            true
        }
    }

    protected fun <T> validateDateField(
        uiState: MutableStateFlow<FieldUiState<T?>>
    ): Boolean {
        uiState.update {
            it.copy(errorCode =
                if (uiState.value.fieldInput == null)
                    ErrorCode.EmptyDate
                else
                    ErrorCode.None
            )
        }
        return uiState.value.errorCode == ErrorCode.None

    }

    fun onRemoveUser(uid: String) {
        uids.update { it.minus(uid) }
    }

    override fun validateAllFields(): Boolean {
        return listOf(
            validateBasicField(title, ErrorCode.EmptyTitle),
            validateDateField(date),
            validateTimeRangeField(startTime, endTime)   ,
            validateUserList(users)
        ).all { it }
    }

    override fun processFieldErrorCode(errorCode: ErrorCode) {

    }

    suspend fun onSubmit(onSuccess: () -> Unit, onFailure: (String) -> Unit, viewModelAction: suspend () -> ErrorCode) {
        if (validateAllFields()) {
            _available.value = false
            if (
                authRepository.refresh() and
                checkUserAvailability(
                    startDate = Timestamp(date.value.fieldInput!!.plusSeconds(startTime.value.fieldInput)),
                    endDate = Timestamp(date.value.fieldInput!!.plusSeconds(endTime.value.fieldInput)),
                    uids = uids.value,
                    onFailure = onFailure
                )
            ) {
                processErrorCode(viewModelAction(),
                    onSuccess
                )
            }
            _available.value = true
        }
    }

    protected abstract suspend fun checkUserAvailability(
        startDate: Timestamp,
        endDate: Timestamp,
        uids: List<String>,
        onFailure: (String) -> Unit
    ): Boolean
}