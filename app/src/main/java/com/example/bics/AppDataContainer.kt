package com.example.bics

import android.content.Context
import com.example.bics.data.repository.auth.FirebaseAuthRepository
import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.repository.profile.FirestoreProfileRepository
import com.example.bics.data.repository.profile.ProfileRepository

interface AppContainer {
    val profileRepository: ProfileRepository
    val authRepository: AuthRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val profileRepository: ProfileRepository by lazy {
        FirestoreProfileRepository()
    }
    override val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository(profileRepository)
    }
}