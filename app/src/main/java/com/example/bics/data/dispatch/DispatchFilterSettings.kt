package com.example.bics.data.dispatch

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DispatchFilterSettings(
    val startDateSeconds: Long? = null,
    val endDateSeconds: Long? = null
): Parcelable
