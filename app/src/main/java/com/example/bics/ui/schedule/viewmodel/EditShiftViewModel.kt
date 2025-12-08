package com.example.bics.ui.schedule.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleFilterSettings
import com.example.bics.data.schedule.ScheduleRepository
import com.example.bics.data.schedule.ScheduleStateKeys
import com.example.bics.data.schedule.Shift
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.format
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EditShiftViewModel(private val scheduleRepository: ScheduleRepository, private val profileRepository: ProfileRepository, authRepository: AuthRepository): ScheduleFormViewModel(scheduleRepository, profileRepository, authRepository) {
    lateinit var shiftID: String

    init {
        viewModelScope.launch {
            _loading.update { true }
            shiftID =
                scheduleRepository
                    .state[scheduleRepository.state.lastIndex - 1][ScheduleStateKeys.ChosenShiftID.key] ?: ""
            val shift = scheduleRepository.getShift(shiftID)
            titleUiState.onValueChanged(shift.title)
            descriptionUiState.onValueChanged(shift.description)
            uids.update { shift.uids }

            dateUiState.onValueChanged(
                newValue = shift
                    .startDate
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
            )

            startTimeUiState.onValueChanged(shift.startDate.seconds - date.value.fieldInput!!.epochSecond)
            endTimeUiState.onValueChanged(shift.endDate.seconds - date.value.fieldInput!!.epochSecond)
            _loading.update { false }
        }
    }

    override suspend fun onSubmit(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        super.onSubmit(onSuccess, onFailure) {
            scheduleRepository.editShift(
                Shift(
                    shiftID = shiftID,
                    startDate = Timestamp(date.value.fieldInput!!.plusSeconds(startTime.value.fieldInput)),
                    endDate = Timestamp(date.value.fieldInput!!.plusSeconds(endTime.value.fieldInput)),
                    title = title.value.fieldInput,
                    description = description.value.fieldInput,
                    uids = uids.value
                )
            )
        }
    }

    override suspend fun checkUserAvailability(
        startDate: Timestamp,
        endDate: Timestamp,
        uids: List<String>,
        onFailure: (String) -> Unit
    ): Boolean {
        val shifts = scheduleRepository.getShifts(
            filterSettings = ScheduleFilterSettings(uids, false),
            startDate = startDate,
            endDate = endDate
        ).filter { it.shiftID != shiftID }
        val pattern = DateTimeFormatter.ofPattern("HH:mm")

        if (shifts.isEmpty()) return true

        val unavailableUserID = shifts[0].uids.intersect(uids).first()
        val unavailableUser = profileRepository.getUser(unavailableUserID)

        onFailure(
            format(
                "%s (%s) is Occupied From %s to %s",
                unavailableUser.username,
                unavailableUserID,
                shifts.first().startDate.toInstant().atZone(ZoneId.systemDefault()).format(pattern),
                shifts.first().endDate.toInstant().atZone(ZoneId.systemDefault()).format(pattern),
            )
        )
        return false
    }
}