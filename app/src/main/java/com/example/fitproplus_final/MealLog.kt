package com.example.fitproplus_final

data class MealLog(
    var foodName: String = "",
    var calories: Float = 0f,
    var timestamp: Long = System.currentTimeMillis() // Set current time during creation
)
