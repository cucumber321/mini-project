package com.example.bics

import com.example.bics.data.dispatch.DispatchRepository
import com.example.bics.data.product.ProductRepository
import com.example.bics.data.repository.auth.FirebaseAuthRepository
import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.repository.profile.FirestoreProfileRepository
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.schedule.ScheduleRepository

interface AppContainer {
    val profileRepository: ProfileRepository
    val authRepository: AuthRepository
    val scheduleRepository: ScheduleRepository
    val dispatchRepository: DispatchRepository
    val productRepository: ProductRepository
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
    override val dispatchRepository: DispatchRepository by lazy {
        DispatchRepository(profileRepository, productRepository)
    }
    override val productRepository: ProductRepository by lazy {
        ProductRepository()
    }
}