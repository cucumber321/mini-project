package com.example.bics.ui.schedule.viewmodel

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bics.bicsApplication
import com.example.bics.ui.home.HomeViewModel

object ScheduleViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val container = this.bicsApplication().container
            ScheduleListViewModel(container.scheduleRepository, container.profileRepository)
        }
        initializer {
            val container = this.bicsApplication().container
            HomeViewModel(container.scheduleRepository, container.profileRepository)
        }
    }
}