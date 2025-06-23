package com.example.wooriga

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApi {

    // 요리법 목록 조회
    @GET("/cultures/{familyId}/recipes")
    suspend fun getRecipes(
        @Path("familyId") familyId: Long,
        @Query("lastRecipeId") lastRecipeId: Long?,
        @Query("pageable") pageable: String = """{"page":0,"size":20,"sort":["id,desc"]}"""
    ): CommonResponse2<RecipeListResponse>

    // 요리법 생성
    @Multipart
    @POST("/cultures/{familyId}/recipes")
    suspend fun createRecipe(
        @Path("familyId") familyId: Long,
        @Part("recipe") recipeJson: RequestBody,
        @Part coverImages: List<MultipartBody.Part>?, // 대표 이미지
        @Part stepImages: List<MultipartBody.Part>? // 조리 이미지 (지금은 null)
    ): CommonResponse2<Any>

    // 요리법 상세
    @GET("/cultures/{familyId}/recipes/{recipeId}")
    suspend fun getRecipeDetail(
        @Path("familyId") familyId: Long,
        @Path("recipeId") recipeId: Long
    ): CommonResponse2<RecipeDetailResponse>

    // 요리법 삭제
    @DELETE("/cultures/{familyId}/recipes/{recipeId}")
    suspend fun deleteRecipe(
        @Path("familyId") familyId: Long,
        @Path("recipeId") recipeId: Long
    ): CommonResponse

}