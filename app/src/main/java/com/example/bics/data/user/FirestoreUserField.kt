package com.example.bics.data.user

enum class FirestoreUserField(val key: String, val default: Any? = null) {
    Collection("users"),
    Username("username", UserDataSource.DEFAULT_USERNAME),
    ProfilePicture("profile_picture", UserDataSource.defaultProfilePicture),
    DisplayID("display_id", "U000"),
    Counter("counter", 1),
    LastID("last_id", 1)
}