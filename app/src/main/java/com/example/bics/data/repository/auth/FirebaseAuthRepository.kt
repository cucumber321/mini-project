package com.example.bics.data.repository.auth

import com.example.bics.data.repository.profile.ProfileRepository
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(private val profileRepository: ProfileRepository): AuthRepository {
    private val auth = Firebase.auth

    private val _user = MutableStateFlow(auth.currentUser)
    private val _error = MutableStateFlow(ErrorCode.None)

    val user = _user.asStateFlow()
    override val error = _error.asStateFlow()

    private val listener = FirebaseAuth.AuthStateListener {
        val currentUser = it.currentUser
        if (currentUser == null) {
            _user.update { currentUser }
        }
        else if (currentUser.isEmailVerified) {
            _user.update { currentUser }
        }
        profileRepository.changeUser(currentUser?.uid ?: "", currentUser?.email?: "")
    }

    init {
        auth.addAuthStateListener(listener)
    }

    override fun getUserStream(): StateFlow<FirebaseUser?> {
        return user
    }

    override suspend fun login(email: String, password: String): ErrorCode {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val currentUser = auth.currentUser
            when {
                currentUser == null -> ErrorCode.Unknown
                !currentUser.isEmailVerified -> {
                    logout()
                    ErrorCode.UnverifiedEmail
                }
                else -> ErrorCode.None
            }
        } catch (e: Exception) {
            ErrorCode.processException(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override suspend fun signup(
        username: String,
        email: String,
        password: String
    ): ErrorCode {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val uid = profileRepository.addUuidToMap(currentUser.uid)
                profileRepository.insertUser(UserProfile(uid = uid, username = username))
                currentUser.sendEmailVerification()
                logout()
            }
            ErrorCode.None
        } catch (e: Exception) {
            ErrorCode.processException(e)
        }
    }

    override suspend fun forgotPassword(email: String): ErrorCode {
        return try {
            auth.sendPasswordResetEmail(email).await()
            ErrorCode.None
        } catch (e: Exception) {
            ErrorCode.processException(e)
        }
    }

    override suspend fun changePassword(password: String): ErrorCode {
        val user = _user.value

        return try {
            if (user == null) ErrorCode.Unknown
            else {
                user.updatePassword(password).await()
                ErrorCode.None
            }
        } catch (e: Exception) {
            ErrorCode.processException(e)
        }
    }

    override suspend fun changeEmail(email: String): ErrorCode {
        val user = _user.value

        return try {
            if (user == null) ErrorCode.Unknown
            else {
                user.verifyBeforeUpdateEmail(email).await()
                ErrorCode.None
            }
        } catch (e: Exception) {
            ErrorCode.processException(e)
        }
    }

    override suspend fun reauthenticate(password: String): ErrorCode {
        val currentUser = _user.value
        return if (currentUser != null) {
            try {
                val credentials = EmailAuthProvider.getCredential(currentUser.email?: return ErrorCode.Unknown, password)
                currentUser.reauthenticate(credentials).await()
                ErrorCode.None
            } catch (e: Exception) {
                ErrorCode.processException(e)
            }
        } else ErrorCode.Unknown
    }

    override fun deleteAccount() {
        val currentUser = _user.value
        if (currentUser != null) {
            currentUser.delete()
            profileRepository.deleteUser()
        }
    }

    override suspend fun refresh(): Boolean {
        return try{
            _error.update { ErrorCode.None }
            _user.value?.reload()?.await()
            true
        } catch (_: FirebaseAuthInvalidUserException) {
            _error.update { ErrorCode.EmailChanged }
            logout()
            false
        } catch (e: Exception) {
            _error.update { ErrorCode.processException(e) }
            false
        }
    }

    override fun isLoggedIn(): Boolean = _user.value != null
}