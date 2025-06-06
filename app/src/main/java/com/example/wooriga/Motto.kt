package com.example.wooriga

data class MottoRequest(
    val familyName: String,
    val motto: String
)

data class Motto(
    val id: Long,
    val title: String,
    val familyName: String,
    val createdAt: String
)

data class MottoListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: MottoListResult
)

data class MottoListResult(
    val mottos: List<Motto>,
    val hasNext: Boolean,
    val nextCursor: String?
)

data class MottoResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Motto
)

data class BasicResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Any?
)
