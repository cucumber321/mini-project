package com.example.bics.ui.schedule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleRepository
import com.example.bics.data.schedule.ScheduleStateKeys
import com.example.bics.data.schedule.Shift
import com.example.bics.data.user.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShiftDetailsViewModel(private val scheduleRepository: ScheduleRepository, private val profileRepository: ProfileRepository): ViewModel() {
    private val _profiles = MutableStateFlow(emptyList<UserProfile>())
    private val _shift = MutableStateFlow(Shift())
    private val _loading = MutableStateFlow(true)

    val shift = _shift.asStateFlow()
    val loading = _loading.asStateFlow()
    val profiles = _profiles.asStateFlow()
    fun loadDetails(shiftID: String) {
        viewModelScope.launch {
            _shift.update { scheduleRepository.getShift(shiftID) }
            _loading.update { false }
            _profiles.update { profileRepository.getUsers(_shift.value.uids).sortedBy { it.username } }
        }
    }

    fun setChosenShiftState(shiftID: String) {
        scheduleRepository.state.last()[ScheduleStateKeys.ChosenShiftID.key] = shiftID
    }

    fun onDelete(shiftID: String) {
        scheduleRepository.deleteShift(shiftID)
    }
}