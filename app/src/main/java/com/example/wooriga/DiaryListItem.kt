package com.example.wooriga

data class DiaryListItem(
    val id: Long,
    val imgUrl: String,
    val title: String
)

data class DiaryListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DiaryListResult
)

data class DiaryListResult(
    val contents: List<DiaryListItem>,
    val hasNext: Boolean
)

