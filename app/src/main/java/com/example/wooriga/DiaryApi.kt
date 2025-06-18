package com.example.wooriga

import retrofit2.Response
import retrofit2.http.GET
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


//    @GET("family-diary")
//    suspend fun getDiary(@Query("diaryId") diaryId: Long): Response<Diary>
//
//    @POST("family-diary")
//    suspend fun postDiary(@Body diaryRequest: DiaryRequest): Response<Unit>
//
//    @DELETE("family-diary")
//    suspend fun deleteDiary(@Query("diaryId") diaryId: Long): Response<Unit>
//
//    @GET("family-diary/comment")
//    suspend fun getComments(@Query("diaryId") diaryId: Long): Response<List<Comment>>
//
//    @POST("family-diary/comment")
//    suspend fun postComment(@Body commentRequest: CommentRequest): Response<Unit>
//
//    @GET("family-diary/re-comment")
//    suspend fun getReComments(@Query("commentId") commentId: Long): Response<List<ReComment>>
//
//    @POST("family-diary/re-comment")
//    suspend fun postReComment(@Body reCommentRequest: ReCommentRequest): Response<Unit>
//
//    @DELETE("family-diary/comment/delete")
//    suspend fun deleteComment(@Query("commentId") commentId: Long): Response<Unit>
//
//    @GET("family-diary/search")
//    suspend fun searchDiary(@Query("title") title: String): Response<List<Diary>>
}
