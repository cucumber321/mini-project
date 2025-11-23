package com.example.bics.data.user

enum class FirestoreUserField(val key: String, val default: Any? = null) {
    Collection("users"),
    Username("username", UserDataSource.DEFAULT_USERNAME),
    ProfilePicture("profile_picture", UserDataSource.defaultProfilePicture),
    Balance("balance", 0f)
}