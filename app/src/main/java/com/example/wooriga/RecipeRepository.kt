package com.example.wooriga

class RecipeRepository {
    private val api = RetrofitClient.recipeApi

    suspend fun fetchRecipes(familyId: Long, lastRecipeId: Long?): List<Recipe> {
        val response = api.getRecipes(familyId, lastRecipeId)
        if (response.isSuccess) {
            return response.result.recipes.map { it.toUiModel() }
        } else {
            throw Exception("API 실패: ${response.message}")
        }
    }
}
