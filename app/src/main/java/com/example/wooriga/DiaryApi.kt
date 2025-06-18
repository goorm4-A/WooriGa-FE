package com.example.wooriga

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface DiaryApi {

    // 일기 목록 조회
    @GET("family-diary/list")
    suspend fun getDiaryList(
        @Query("pageable.page") page: Int,
        @Query("pageable.size") size: Int,
        @Query("pageable.sort") sort: List<String>,
        @Query("familyId") familyId: Long,
        @Query("familyDiaryId") lastDiaryId: Long? = null
    ): Response<DiaryListResponse>

    // 일기 상세 조회
    @GET("family-diary")
    suspend fun getDiaryDetail(
        @Query("diaryId") diaryId: Long
    ): Response<DiaryDetailResponse>

    // 일기 등록
    @Multipart
    @POST("family-diary")
    suspend fun postFamilyDiary(
        @Part("familyDiaryDto") familyDiaryDto: RequestBody,
        @Part images: List<MultipartBody.Part>?
    ): Response<DiaryDetailResponse>

    // 일기 검색
    @GET("family-diary/search")
    suspend fun searchDiaries(
        @Query("familyId") familyId: Long,
        @Query("keyword") keyword: String,
        @Query("lastDiaryId") lastDiaryId: Long? = null,
        @Query("pageable.page") page: Int = 0,
        @Query("pageable.size") size: Int = 20,
        @Query("pageable.sort") sort: List<String> = listOf("createdAt,desc")
    ): Response<DiaryListResponse>

}
