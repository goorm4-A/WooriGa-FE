package com.example.wooriga

data class DiaryComment(
    val content: String,
    val createdAt: String,
    val username: String,
    val familyMemberId: Long,
    val familyDiaryId: Long
)

data class CommentResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: CommentResult
)

data class CommentResult(
    val comments: List<DiaryComment>,
    val hasNext: Boolean
)

data class CommentPostRequest(
    val content: String,
    val createdAt: String
)

data class CommentPostResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DiaryComment
)

