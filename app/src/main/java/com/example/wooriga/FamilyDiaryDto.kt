package com.example.wooriga

data class FamilyDiaryDto(
    val familyId: Long,
    val username: String,
    val profile: String,
    val title: String,
    val location: String,
    val description: String,
    val contentType: String = "DIARY", // 고정
    val diaryTags: List<String>,
    val participantIds: List<Long> // 지금은 더미로 넣어도 되고, 나중에 사용자 ID에 맞게 교체
)
