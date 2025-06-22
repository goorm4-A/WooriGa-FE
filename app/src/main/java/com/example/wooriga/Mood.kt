package com.example.wooriga

// UI에서 쓰는 로컬 모델
data class Mood(
    val id: Long,
    val familyId: Long,
    val moodType: String,
    val tags: List<String>
)

// API 응답용 DTO
data class MoodResponse(
    val id: Long,
    val tags: List<String>,
    val moodType: String
)