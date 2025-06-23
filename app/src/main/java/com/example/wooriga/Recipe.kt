package com.example.wooriga

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val cookTimeMinutes: Int,
    val coverImageUrl: String?,
    val ingredients: List<String>,
    val steps: List<CookingStep>
) : Parcelable

@Parcelize
data class CookingStep(
    val stepNumber: Int,
    val description: String,
    val imageUrl: String? = null
) : Parcelable

data class RecipeListResponse(
    val recipes: List<RecipeDto>,
    val hasNext: Boolean,
    val nextCursor: String?
)

data class RecipeDto(
    val id: Long,
    val userName: String,
    val title: String,
    val cookingTime: Int,
    val coverImage: String?
) {
    fun toUiModel() = Recipe(
        id = id.toString(),
        title = title,
        author = userName,
        description = "", // 필요 시 상세 조회에서 설정
        cookTimeMinutes = cookingTime,
        coverImageUrl = coverImage,
        ingredients = emptyList(), // 상세에서 설정
        steps = emptyList()
    )
}
