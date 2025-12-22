package com.example.bics.data.dispatch

import com.google.firebase.Timestamp

data class Dispatch(
    val id: String = "",
    val orderedBy: String = "",
    val dateCreated: Timestamp = Timestamp.now(),
    val createdBy: String = "",
    var lastModifiedBy: String? = null,
    var lastModifiedDate: Timestamp? = null,
    val items: List<DispatchItem> = emptyList(),
)