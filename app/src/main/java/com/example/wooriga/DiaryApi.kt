package com.example.wooriga

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    // 댓글 조회
    @GET("family-diary/comment")
    suspend fun getDiaryComments(
        @Query("familyDiaryId") diaryId: Long,
        @Query("pageable.page") page: Int,
        @Query("pageable.size") size: Int,
        @Query("pageable.sort") sort: List<String> = listOf("createdAt,desc")
    ): Response<CommentResponse>

    // 댓글 작성
    @POST("family-diary/comment")
    suspend fun postDiaryComment(
        @Query("diaryId") diaryId: Long,
        @Query("familyMemberId") familyMemberId: Long,
        @Body request: CommentPostRequest
    ): Response<CommentPostResponse>

    // 댓글 삭제
    @DELETE("/family-diary/comment/delete")
    suspend fun deleteComment(
        @Query("commentId") commentId: Long
    ): Response<CommonResponse>

    // 대댓글 조회
    @GET("/family-diary/re-comment")
    suspend fun getReComments(
        @Query("commentId") commentId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<CommentResponse>

    // 일기 등록
    @Multipart
    @POST("family-diary")
    suspend fun postFamilyDiary(
        @Part("familyDiaryDto") familyDiaryDto: RequestBody,
        @Part images: List<MultipartBody.Part>?
    ): Response<CommonResponse>

    // 일기 삭제
    @DELETE("family-diary")
    suspend fun deleteDiary(
        @Query("diaryId") diaryId: Long
    ): Response<CommonResponse>

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
