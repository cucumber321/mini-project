package com.example.bics.ui.schedule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleFilterSettings
import com.example.bics.data.schedule.ScheduleRepository
import com.example.bics.data.schedule.ScheduleStateKeys
import com.example.bics.data.user.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class FilterScheduleViewModel(private val scheduleRepository: ScheduleRepository, private val profileRepository: ProfileRepository): ViewModel() {
    private val filterSettings = scheduleRepository
        .state[scheduleRepository.state.lastIndex - 1]
        .getMutableStateFlow(ScheduleStateKeys.FilterSettings.key, ScheduleFilterSettings())
    private val tempUids = scheduleRepository
        .state
        .last()
        .getMutableStateFlow(ScheduleStateKeys.UsersAssigned.key, filterSettings.value.users.toList())
    private val _users = MutableStateFlow<List<UserProfile>>(emptyList())
    private val _includeAll = MutableStateFlow(filterSettings.value.includeAll)
    val users = _users.asStateFlow()
    val includeAll = _includeAll.asStateFlow()

    init {
        tempUids.onEach { uidList ->
            _users.update {profileRepository.getUsers(uidList).sortedBy { it.username }}
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        scheduleRepository.state.removeAt(scheduleRepository.state.lastIndex)
    }

    fun onApply() {
        filterSettings.update { it.copy(users = tempUids.value.toList(), includeAll = _includeAll.value) }
    }

    fun onRemoveUser(uid: String) {
        tempUids.update {
            it.filter { u -> u != uid }
        }
    }

    fun onIncludeAllSelected(value: Boolean) {
        _includeAll.update { value }
    }

    fun reset() {
        tempUids.update { listOf(profileRepository.getUserStream().value.uid) }
        _includeAll.update { false }
    }
}