package com.example.bics.ui.schedule.viewmodel

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bics.bicsApplication
import com.example.bics.ui.home.HomeViewModel

object ScheduleViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            val container = this.bicsApplication().container
            container.scheduleRepository.state.add(this.createSavedStateHandle())
            ScheduleListViewModel(container.scheduleRepository, container.profileRepository)
        }
        initializer {
            val container = this.bicsApplication().container
            container.scheduleRepository.state.add(this.createSavedStateHandle())
            HomeViewModel(container.scheduleRepository, container.profileRepository)
        }
        initializer {
            val container = this.bicsApplication().container
            container.scheduleRepository.state.add(this.createSavedStateHandle())
            FilterScheduleViewModel(container.scheduleRepository, container.profileRepository)
        }
        initializer {
            val container = this.bicsApplication().container
            SelectUsersViewModel(container.scheduleRepository,container.profileRepository)
        }
        initializer {
            val container = this.bicsApplication().container
            container.scheduleRepository.state.add(this.createSavedStateHandle())
            AddShiftViewModel(container.scheduleRepository, container.profileRepository, container.authRepository)
        }
        initializer {
            val container = this.bicsApplication().container
            container.scheduleRepository.state.add(this.createSavedStateHandle())
            EditShiftViewModel(container.scheduleRepository, container.profileRepository, container.authRepository)
        }
        initializer {
            val container = this.bicsApplication().container
            ShiftDetailsViewModel(container.scheduleRepository, container.profileRepository)
        }
    }
}