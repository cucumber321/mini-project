package com.example.bics.data.schedule

import com.google.firebase.Timestamp

data class Shift (
    val shiftID: String = "",
    val date: String = "",
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),
    val title: String = "",
    val description: String = "",
    var usersAssigned: List<String> = emptyList()
)