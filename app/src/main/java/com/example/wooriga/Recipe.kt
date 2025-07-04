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

data class RecipeRequest(
    val title: String,
    val description: String,
    val cookingTime: Int,
    val ingredients: List<String>,
    val steps: List<RecipeStepRequest>
)

data class RecipeStepRequest(
    val description: String,
    val imageIndexes: Int? = null // 지금은 null 가능
)

data class RecipeDetailResponse(
    val userName: String,
    val title: String,
    val description: String,
    val cookingTime: Int,
    val coverImages: List<String>,
    val ingredients: List<String>,
    val steps: List<RecipeStepDetail>
)

data class RecipeStepDetail(
    val description: String,
    val imageUrl: String?
)

fun RecipeDetailResponse.toUiModel(id: Long): Recipe {
    return Recipe(
        id = id.toString(),
        title = title,
        author = userName,
        description = description,
        cookTimeMinutes = cookingTime,
        coverImageUrl = coverImages.firstOrNull(),
        ingredients = ingredients,
        steps = steps.mapIndexed { index, step ->
            CookingStep(
                stepNumber = index + 1,
                description = step.description,
                imageUrl = step.imageUrl
            )
        }
    )
}
