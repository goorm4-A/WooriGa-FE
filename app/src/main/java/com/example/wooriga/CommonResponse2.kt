package com.example.wooriga

data class CommonResponse2<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T
)
