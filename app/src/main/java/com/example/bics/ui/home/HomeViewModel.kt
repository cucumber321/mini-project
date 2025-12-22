package com.example.bics.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleFilterSettings
import com.example.bics.data.schedule.ScheduleRepository
import com.example.bics.data.schedule.Shift
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class HomeViewModel(private val scheduleRepository: ScheduleRepository, private val profileRepository: ProfileRepository): ViewModel() {
    private val _shifts = MutableStateFlow(emptyList<MutableStateFlow<Shift>>())
    private val _isRefreshing = MutableStateFlow(false)

    val shifts = _shifts.asStateFlow()
    val isRefreshing = _isRefreshing.asStateFlow()
    val user = profileRepository.getUserStream()

    override fun onCleared() {
        super.onCleared()
        scheduleRepository.state.removeAt(scheduleRepository.state.lastIndex)
    }

    fun refresh() {
        viewModelScope.launch {

            val todayDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
            _isRefreshing.update { true }
            _shifts.update {
                scheduleRepository.getShifts(
                    ScheduleFilterSettings(listOf(user.value.uid), false),
                    Timestamp(todayDate),
                    Timestamp(todayDate.plusSeconds(24 * 60 * 60))
                ).map { MutableStateFlow(it) }
            }

            val uids = _shifts.value.flatMap { it.value.uids }.toSet().toList()

            val profiles = profileRepository.getUsers(uids).associateBy { it.uid }

            _shifts.value.forEach {
                it.update { s -> s.copy(profiles = s.uids.mapNotNull {uid -> profiles[uid] }) }
            }

            delay(100)
            _isRefreshing.update { false }
        }
    }
}