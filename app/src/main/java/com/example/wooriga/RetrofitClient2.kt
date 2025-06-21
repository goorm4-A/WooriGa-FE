package com.example.wooriga

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient2 {
    private const val BASE_URL = "http://54.180.104.168:8081/"

    private val client = okhttp3.OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor()) // 자동으로 Authorization 헤더 추가
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val annivApi: ApiService = retrofit.create(ApiService::class.java)
    val familyGroupApi: ApiService = retrofit.create(ApiService::class.java)


    private val retrofit2 = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userApi: ApiService = retrofit2.create(ApiService::class.java)


}