package com.example.bics.data.repository.auth

import com.example.bics.data.user.ErrorCode
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    val error: StateFlow<ErrorCode>
    fun getUserStream(): StateFlow<FirebaseUser?>

    suspend fun login(email: String, password: String): ErrorCode

    fun logout()
    suspend fun signup(username: String, email: String, password: String): ErrorCode
    suspend fun forgotPassword(email: String): ErrorCode
    suspend fun changePassword(password: String): ErrorCode
    suspend fun changeEmail(email: String): ErrorCode
    suspend fun reauthenticate(password: String): ErrorCode

    fun deleteAccount()
    suspend fun refresh(): Boolean

    fun isLoggedIn(): Boolean
}