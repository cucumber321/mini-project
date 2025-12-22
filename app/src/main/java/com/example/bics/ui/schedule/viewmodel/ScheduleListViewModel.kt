package com.example.bics.ui.schedule.viewmodel

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleFilterSettings
import com.example.bics.data.schedule.ScheduleRepository
import com.example.bics.data.schedule.ScheduleStateKeys
import com.example.bics.data.schedule.Shift
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
class ScheduleListViewModel(private val scheduleRepository: ScheduleRepository, private val profileRepository: ProfileRepository): ViewModel() {
    private var scheduleDate = Timestamp(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
    val filterSettings = scheduleRepository.state.last().getMutableStateFlow(ScheduleStateKeys.FilterSettings.key,
        ScheduleFilterSettings(listOf(profileRepository.getUserStream().value.uid)))

    private val _shifts = MutableStateFlow(emptyList<MutableStateFlow<Shift>>())
    private val _isRefreshing = MutableStateFlow(false)
    val shifts = _shifts.asStateFlow()
    val isRefreshing = _isRefreshing.asStateFlow()

    override fun onCleared() {
        super.onCleared()
        scheduleRepository.state.removeAt(scheduleRepository.state.lastIndex)
    }

    fun onDateSelected(instant: Instant) {
        scheduleDate = Timestamp(instant)
        viewModelScope.launch { updateShifts() }
    }

    suspend fun updateShifts() {
        _isRefreshing.update { true }
        _shifts.update {
            scheduleRepository.getShifts(
                filterSettings.value,
                scheduleDate,
                Timestamp(scheduleDate.toInstant().plusSeconds(24 * 60 * 60))
            ).map { MutableStateFlow(it) }
        }

        val uids = _shifts.value.flatMap { it.value.uids }.toSet().toList()

        val profiles = profileRepository.getUsers(uids).associateBy { it.uid }

        _shifts.value.forEach {
            it.update { s -> s.copy(profiles = s.uids.mapNotNull {uid -> profiles[uid] }.sortedBy { it.username }) }
        }

        delay(100)
        _isRefreshing.update { false }
    }

    fun refresh() {
        viewModelScope.launch {
            updateShifts()
        }
    }
}