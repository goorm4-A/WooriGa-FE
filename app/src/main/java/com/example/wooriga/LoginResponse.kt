package com.example.wooriga

data class ApiResponse<T>(
    val code: String,
    val message: String,
    val result: T
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val result: ResultData
)

data class ResultData(
    val userInfo: UserManager.UserInfo
)
