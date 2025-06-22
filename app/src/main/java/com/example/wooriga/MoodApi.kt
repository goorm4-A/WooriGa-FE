package com.example.wooriga

import retrofit2.http.GET
import retrofit2.http.Path

interface MoodApi {

    // 분위기 목록 조회
    @GET("cultures/{familyId}/moods")
    suspend fun getFamilyMoods(
        @Path("familyId") familyId: Long
    ): CommonResponse2<List<MoodResponse>>
}