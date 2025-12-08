package com.example.bics

import com.example.bics.data.repository.auth.FirebaseAuthRepository
import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.repository.profile.FirestoreProfileRepository
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleRepository

interface AppContainer {
    val profileRepository: ProfileRepository
    val authRepository: AuthRepository
    val scheduleRepository: ScheduleRepository
}

class AppDataContainer() : AppContainer {
    override val profileRepository: ProfileRepository by lazy {
        FirestoreProfileRepository()
    }
    override val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository(profileRepository)
    }
    override val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepository()
    }
}