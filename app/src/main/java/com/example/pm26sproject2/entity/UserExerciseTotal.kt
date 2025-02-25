package com.example.pm26sproject2.entity

data class UserExerciseTotal(
    var userId: String,
    var calories: Double,
    var duration: Long,
    var steps: Long
)