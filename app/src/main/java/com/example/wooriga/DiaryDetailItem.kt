package com.example.wooriga

data class DiaryDetailItem(
    val diaryId: Long,
    val title: String,
    val location: String,
    val description: String,
    val contentType: String,
    val diaryTags: List<DiaryTag>,
    val participantIds: List<Long>,
    val imgUrls: List<String>
)

data class DiaryDetailResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DiaryDetailItem
)

