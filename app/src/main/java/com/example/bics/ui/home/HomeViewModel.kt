package com.example.bics.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleRepository
import com.example.bics.data.schedule.Shift
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(scheduleRepository: ScheduleRepository, profileRepository: ProfileRepository): ViewModel() {

    val user = profileRepository.getUserStream()
    val schedules = MutableStateFlow(emptyList<Shift>())

    init {
        viewModelScope.launch {
            schedules.update {
                scheduleRepository.getShifts(listOf(user.value.uid), LocalDate.now().toString())
            }
        }
    }
}