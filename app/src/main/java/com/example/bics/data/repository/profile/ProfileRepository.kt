package com.example.bics.data.repository.profile

import android.net.Uri
import com.example.bics.data.user.FirestoreUserField
import com.example.bics.data.user.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    fun getUserStream(): StateFlow<UserProfile>
    suspend fun getUserList(): List<UserProfile>

    fun updateUser(key: FirestoreUserField, value: Any)

    fun deleteUser()

    suspend fun updateProfilePicture(selectedUri: Uri)

    fun changeUser(uid: String, email: String)

    fun insertUser(profile: UserProfile)
    suspend fun getUser(uid: String): UserProfile
    suspend fun getUsers(uids: List<String>): List<UserProfile>
    suspend fun addUuidToMap(uid: String): String
}