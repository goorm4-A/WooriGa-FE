package com.example.wooriga

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("oauth/kakao")
    fun loginWithKakao(@Query("code") code: String): Call<LoginResponse>
}
