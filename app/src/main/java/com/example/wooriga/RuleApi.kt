package com.example.wooriga

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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

    // 규칙 등록
    @POST("cultures/rule")
    suspend fun addRule(
        @Body body: RuleRequest
    ): BasicResponse

    // 규칙 상세
    @GET("cultures/rule/{ruleId}")
    suspend fun getRuleDetail(
        @Path("ruleId") ruleId: Long
    ): RuleDetailResponse

    // 규칙 삭제
    @DELETE("cultures/rule/{ruleId}")
    suspend fun deleteRule(@Path("ruleId") ruleId: Long): BasicResponse

}

