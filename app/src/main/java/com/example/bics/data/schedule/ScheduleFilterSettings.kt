package com.example.bics.data.schedule

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleFilterSettings (
    val users: List<String> = emptyList(),
    val includeAll: Boolean = false
) : Parcelable
