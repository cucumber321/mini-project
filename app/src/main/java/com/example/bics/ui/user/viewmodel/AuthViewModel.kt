package com.example.bics.ui.user.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.user.UserProfile

class AuthViewModel(private val authRepository: AuthRepository, private val profileRepository: ProfileRepository): ViewModel() {
    val user = authRepository.getUserStream()
    val profile = profileRepository.getUserStream()
    val error = authRepository.error
    fun logout() {
        authRepository.logout()
    }

    suspend fun refreshUser(): Boolean {
        return authRepository.refresh()
    }

    fun deleteAccount() {
        authRepository.deleteAccount()
    }

    suspend fun getBookedUserProfile(uid: String): UserProfile {
        return profileRepository.getUser(uid)
    }
}