package com.example.bics.data.schedule

import com.example.bics.data.repository.profile.ProfileRepository
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class ScheduleRepository(private val profileRepository: ProfileRepository) {
    val firestore = Firebase.firestore.collection("schedule")
    suspend fun getShifts(usersAssigned: List<String>, date: String): List<Shift> {
        val docs = firestore.document(date).collection("shifts").whereArrayContainsAny("users_assigned", usersAssigned).get().await()

        @Suppress("UNCHECKED_CAST")
        val shifts = docs.mapNotNull {
            Shift(
                shiftID = it.id,
                date = date,
                startTime = it.getTimestamp("start_time") ?: Timestamp.now(),
                endTime = it.getTimestamp("end_time") ?: Timestamp.now(),
                title = it.getString("title") ?: "",
                description = it.getString("description") ?: "",
                usersAssigned = it.get("users_assigned") as (List<String>)
            )
        }

        for (shift in shifts) {
            shift.usersAssigned = shift.usersAssigned.map { uid ->
                profileRepository.getUser(uid).username
            }.sorted()
        }

        return shifts
    }


}