package com.example.bics.data.schedule

enum class FirestoreScheduleField(val key: String) {
    Collection("schedule"),
    Title("title"),
    Description("Description"),
    StartDate("start_date"),
    EndDate("end_date"),
    UsersAssigned("users_assigned"),
    Counter("counter"),
    LastID("last_id")
}