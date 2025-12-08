package com.example.bics.data.schedule

import com.example.bics.data.user.UserProfile
import com.google.firebase.Timestamp

data class Shift (
    val shiftID: String = "",
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp = Timestamp.now(),
    val title: String = "",
    val description: String = "",
    val uids: List<String> = emptyList(),
    val profiles: List<UserProfile> = emptyList()
)