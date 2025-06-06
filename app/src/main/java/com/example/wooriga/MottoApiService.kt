package com.example.wooriga

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MottoApiService {

    // 가훈 전체 조회
    @GET("/culture/motto")
    suspend fun getMottos(
        @Query("familyId") familyId: Long,
        @Query("userId") userId: Long,
        @Query("cursor") cursor: String? = null
    ): Response<MottoListResponse>

    // 가훈 등록
    @POST("/culture/motto")
    suspend fun addMotto(
        @Query("userId") userId: Long,
        @Body body: MottoRequest
    ): Response<BasicResponse>

    // 가훈 삭제
    @DELETE("/culture/motto/{mottoId}")
    suspend fun deleteMotto(
        @Path("mottoId") mottoId: Long,
        @Query("userId") userId: Long
    ): Response<BasicResponse>

    // 가훈 수정
    @PATCH("/culture/motto/{mottoId}")
    suspend fun updateMotto(
        @Path("mottoId") mottoId: Long,
        @Query("userId") userId: Long,
        @Body body: MottoRequest
    ): Response<MottoResponse>
}
