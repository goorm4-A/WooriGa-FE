package com.example.wooriga

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    @GET("/oauth/success")
    fun loginWithKakao(@Query("code") code: String): Call<LoginResponse>

    @PUT("/users/me/info")
    fun updateUserInfo(
        @Header("Authorization") bearerToken: String,
        @Body body: Map<String, String>
    ): Call<Void>

    @GET("/users/me/info")
    fun getUserInfo(
        @Header("Authorization") bearerToken: String
    ): Call<ApiResponse<UserManager.UserInfo>>
}
