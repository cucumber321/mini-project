package com.example.bics.ui.schedule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleRepository
import com.example.bics.data.schedule.ScheduleStateKeys
import com.example.bics.data.user.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectUsersViewModel(scheduleRepository: ScheduleRepository, private val profileRepository: ProfileRepository): ViewModel() {
    private val uids = scheduleRepository
        .state
        .last()
        .getMutableStateFlow<List<String>>(ScheduleStateKeys.UsersAssigned.key, emptyList())
    private val _chosen = MutableStateFlow<List<UserProfile>>(emptyList())
    private val _search = MutableStateFlow("")
    private val _userList = MutableStateFlow<List<UserProfile>>(emptyList())
    val userList = _userList.asStateFlow()
    val chosen = _chosen.asStateFlow()
    val search = _search.asStateFlow()

    init {
        viewModelScope.launch {
            _userList.update { profileRepository.getUserList() }
            _chosen.update { _userList.value.filter { uids.value.contains(it.uid) } }
        }
    }

    fun onSearch(value: String) {
        _search.update { value }
    }

    fun onConfirm() {
        uids.update { _chosen.value.map { it.uid } }
    }

    fun onSelect(profile: UserProfile) {
        if (_chosen.value.contains(profile)) onRemove(profile)
        else _chosen.update { (it + profile).sortedBy { u -> u.username } }
    }

    fun onRemove(profile: UserProfile) {
        _chosen.update { it.filter { u -> u != profile } }
    }
}