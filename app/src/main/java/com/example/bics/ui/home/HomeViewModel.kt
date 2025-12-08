package com.example.bics.ui.home

import androidx.lifecycle.ViewModel
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleRepository
import com.example.bics.data.schedule.Shift
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(scheduleRepository: ScheduleRepository, profileRepository: ProfileRepository): ViewModel() {

    val user = profileRepository.getUserStream()
    val schedules = MutableStateFlow(emptyList<Shift>())

    init {
//        viewModelScope.launch {
//            schedules.update {
//                scheduleRepository.getShifts(listOf(user.value.uid), Timestamp(LocalDate.now().atStartOfDay().atZone(
//                    ZoneId.systemDefault()).toInstant()))
//            }
//        }
    }
}