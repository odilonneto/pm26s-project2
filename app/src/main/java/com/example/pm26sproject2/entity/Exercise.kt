package com.example.pm26sproject2.entity

data class Exercise(
    var userId: String,
    var calories: Double,
    var duration: Long,
    var startTime: Long,
    var endTime: Long,
    var steps: Int
)