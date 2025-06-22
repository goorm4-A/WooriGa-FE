package com.example.wooriga

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MoodApi {

    // 분위기 목록 조회
    @GET("cultures/{familyId}/moods")
    suspend fun getFamilyMoods(
        @Path("familyId") familyId: Long
    ): CommonResponse2<List<MoodResponse>>

    // 분위기 등록
    @POST("cultures/{familyId}/moods")
    suspend fun postFamilyMood(
        @Path("familyId") familyId: Long,
        @Body request: MoodRequest
    ): CommonResponse2<MoodResponse>

    // 분위기 삭제
    @DELETE("cultures/{familyId}/moods/{moodId}")
    suspend fun deleteMood(
        @Path("familyId") familyId: Long,
        @Path("moodId") moodId: Long
    ): CommonResponse2<String>

}