package com.example.fitproplus_final.models

data class WorkoutLog(
    val exerciseName: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val duration: Int = 0,  // Duration of the workout )
    val timestamp: Long = 0L // Time of logging the workout
)
