package com.example.bics.data.user

import android.net.Uri


data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val username: String = UserDataSource.DEFAULT_USERNAME,
    val profilePictureUri: Uri = UserDataSource.defaultProfilePicture,
    val balance: Double = 0.0,
    val isAdmin: Boolean = false
)