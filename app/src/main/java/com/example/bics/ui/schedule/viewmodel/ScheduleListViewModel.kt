package com.example.bics.ui.schedule.viewmodel

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleRepository
import com.example.bics.data.schedule.Shift
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
class ScheduleListViewModel(private val scheduleRepository: ScheduleRepository, profileRepository: ProfileRepository): ViewModel() {
    private val _scheduleDate = MutableStateFlow(LocalDate.now(
        ZoneOffset.UTC).toString())
    val scheduleDate = _scheduleDate.asStateFlow()
    val usersInvolved = listOf(profileRepository.getUserStream().value.uid)
    private val _shifts = MutableStateFlow(emptyList<Shift>())
    val shifts = _shifts.asStateFlow()

    init {
        viewModelScope.launch {
            _shifts.update {
                scheduleRepository.getShifts(usersInvolved, scheduleDate.value)
            }
        }
    }

    fun onDateSelected(selectedDateMillis: Long) {
        _scheduleDate.update {
            Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                .toString()
        }

        viewModelScope.launch {
            _shifts.update {
                scheduleRepository.getShifts(usersInvolved, scheduleDate.value)
            }
        }
    }

}