package com.example.wooriga

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserInfo
)

data class UserInfo(
    val id: Long,
    val nickname: String,
    val email: String?
)

