package com.example.wooriga

import com.example.wooriga.model.Anniversary
import com.example.wooriga.model.FamilyGroupResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {
    @GET("/oauth/success")
    fun loginWithKakao(@Query("code") code: String): Call<LoginResponse>

    // 유저 정보 수정
    @PUT("/users/me/info")
    fun updateUserInfo(
        @Header("Authorization") bearerToken: String,
        @Body body: Map<String, String>
    ): Call<Void>

    // 유저 정보 조회
    @GET("/users/me/info")
    fun getUserInfo(
        @Header("Authorization") bearerToken: String
    ): Call<ApiResponse<UserManager.UserInfo>>

    // 기념일 등록
    @POST("/anniversary")
    suspend fun addAnniversary(
        @Body anniv: Anniversary
    ): Response<CommonResponse2<Anniversary>>


    // 기념일 조회
    @GET("/anniversary/search")
    suspend fun getAnniversaries(
        @Query("type") type: String?,
        @Query("lastAnniversaryId") lastId: Long?,
        @QueryMap pageable: Map<String, String>
    ): Response<CommonResponse2<AnniversaryResult>>


    // 기념일 상세보기
    @GET("/anniversary/detail")
    suspend fun getAnniversaryDetail(
        @Query("anniversaryId") anniversaryId: Int
    ): Anniversary

    // 가족 그룹 생성 (텍스트+이미지파일)
    @Multipart
    @POST("/groups")
    fun createGroup(
        @Part("name") name: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<ApiResponse<FamilyGroupResponse>>

    // 가족 그룹 목록 조회
    @GET("/groups")
    fun getGroups(
    ): Call<ApiResponse<List<FamilyGroupResponse>>>
}
