package com.example.wooriga

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RuleApi {

    // 규칙 목록 조회
    @GET("cultures/{familyId}/rule")
    suspend fun getRules(
        @Path("familyId") familyId: Long,
        @Query("userId") userId: Long,
        @Query("cursor") cursor: String? = null
    ): RuleResponse

}

