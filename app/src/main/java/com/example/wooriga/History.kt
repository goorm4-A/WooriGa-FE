package com.example.wooriga.model

import java.time.LocalDate

data class History(
    val family: String,
    val dateString: String,        // "2025.06.02" - 표시용
    val dateObject: LocalDate,     // 정렬 - 연산용
    val title: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double
)