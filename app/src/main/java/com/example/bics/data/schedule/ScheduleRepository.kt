package com.example.bics.data.schedule

import androidx.lifecycle.SavedStateHandle
import com.example.bics.data.user.ErrorCode
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import okhttp3.internal.format

class ScheduleRepository() {
    val firestore = Firebase.firestore.collection(FirestoreScheduleField.Collection.key)
    val state: MutableList<SavedStateHandle> = mutableListOf()

    suspend fun getShifts(
        filterSettings: ScheduleFilterSettings,
        startDate: Timestamp,
        endDate: Timestamp
    ): List<Shift> {
        if (filterSettings.users.isEmpty()) return emptyList()

        val docs = firestore
                .whereLessThan(FirestoreScheduleField.StartDate.key, endDate)
                .whereGreaterThan(FirestoreScheduleField.EndDate.key, startDate)
                .whereArrayContainsAny(FirestoreScheduleField.UsersAssigned.key, filterSettings.users)
                .orderBy(FirestoreScheduleField.StartDate.key)
                .get().await()

        val shifts = docs.map {
            docToShift(it)
        }

        return if (filterSettings.includeAll) shifts.filter { it.uids.containsAll(filterSettings.users) } else shifts
    }

    suspend fun addShift(shift: Shift): ErrorCode {
        return try {
            val doc = firestore.document(FirestoreScheduleField.Counter.key)
            val lastID = doc.get().await().getLong(FirestoreScheduleField.LastID.key) ?: 1
            val shiftID = format("S%03d", lastID)
            firestore.document(shiftID).set(mapOf(
                FirestoreScheduleField.StartDate.key to shift.startDate,
                FirestoreScheduleField.EndDate.key to shift.endDate,
                FirestoreScheduleField.Title.key to shift.title,
                FirestoreScheduleField.Description.key to shift.description,
                FirestoreScheduleField.UsersAssigned.key to shift.uids
            ))
            doc.update(FirestoreScheduleField.LastID.key, lastID + 1)
            ErrorCode.None
        } catch (e: Exception) {
            ErrorCode.processException(e)
        }
    }

    fun editShift(shift: Shift): ErrorCode {
        return try {
            firestore.document(shift.shiftID).set(mapOf(
                FirestoreScheduleField.StartDate.key to shift.startDate,
                FirestoreScheduleField.EndDate.key to shift.endDate,
                FirestoreScheduleField.Title.key to shift.title,
                FirestoreScheduleField.Description.key to shift.description,
                FirestoreScheduleField.UsersAssigned.key to shift.uids
            ))
            ErrorCode.None
        } catch (e: Exception) {
            ErrorCode.processException(e)
        }
    }

    suspend fun getShift(shiftID: String): Shift {
        return docToShift(firestore.document(shiftID).get().await())
    }

    @Suppress("UNCHECKED_CAST")
    fun docToShift(doc: DocumentSnapshot): Shift {
        return Shift(
            shiftID = doc.id,
            startDate = doc.getTimestamp(FirestoreScheduleField.StartDate.key) ?: Timestamp.now(),
            endDate = doc.getTimestamp(FirestoreScheduleField.EndDate.key) ?: Timestamp.now(),
            title = doc.getString(FirestoreScheduleField.Title.key) ?: "",
            description = doc.getString(FirestoreScheduleField.Description.key) ?: "",
            uids = doc.get(FirestoreScheduleField.UsersAssigned.key) as (List<String>)
        )
    }

    fun deleteShift(shiftID: String) {
        firestore.document(shiftID).delete()
    }

}