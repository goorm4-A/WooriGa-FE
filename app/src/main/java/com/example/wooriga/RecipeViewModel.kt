package com.example.wooriga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecipeViewModel : ViewModel() {

    private val _recipeList = MutableLiveData<List<Recipe>>()
    val recipeList: LiveData<List<Recipe>> = _recipeList

    init {
        loadDummyRecipes()
    }

    private fun loadDummyRecipes() {
        val dummyList = listOf(
            Recipe(
                id = "1",
                title = "쉬운 김치볶음밥 만드는 법 알려줄게",
                author = "김숙명",
                description = "매콤하고 맛있는 한끼",
                cookTimeMinutes = 60,
                coverImageUrl = null,
                ingredients = listOf(
                    "밥 1공기",
                    "김치 1/2컵",
                    "대파 1/2대",
                    "참기름 1큰술",
                    "고추장 1작은술"
                ),
                steps = listOf(
                    CookingStep(
                        stepNumber = 1,
                        description = "팬에 참기름을 두르고 대파를 볶아 향을 낸다.",
                        imageUrl = null
                    ),
                    CookingStep(
                        stepNumber = 2,
                        description = "김치를 넣고 중불에서 3분간 볶는다.",
                        imageUrl = null
                    ),
                    CookingStep(
                        stepNumber = 3,
                        description = "밥을 넣고 고추장과 함께 골고루 볶는다.",
                        imageUrl = null
                    ),
                    CookingStep(
                        stepNumber = 4,
                        description = "그릇에 담고 계란 프라이와 함께 낸다.",
                        imageUrl = null
                    )
                )
            ),
            Recipe(
                id = "2",
                title = "양념 두부 조림",
                author = "김숙명",
                description = "두부에 간이 쏙쏙~",
                cookTimeMinutes = 45,
                coverImageUrl = null,
                ingredients = listOf(
                    "두부 1모",
                    "간장 3큰술",
                    "고춧가루 1큰술",
                    "다진 마늘 1작은술",
                    "물 1/2컵"
                ),
                steps = listOf(
                    CookingStep(
                        stepNumber = 1,
                        description = "두부를 적당한 크기로 썬 후 기름에 앞뒤로 노릇하게 굽는다.",
                        imageUrl = null
                    ),
                    CookingStep(
                        stepNumber = 2,
                        description = "양념장 재료(간장, 고춧가루, 마늘, 물)를 섞는다.",
                        imageUrl = null
                    ),
                    CookingStep(
                        stepNumber = 3,
                        description = "구운 두부에 양념장을 부어 중불에서 졸인다.",
                        imageUrl = null
                    ),
                    CookingStep(
                        stepNumber = 4,
                        description = "양념이 반쯤 졸아들면 불을 끈다.",
                        imageUrl = null
                    )
                )
            )
        )
        _recipeList.value = dummyList
    }

    fun addRecipe(recipe: Recipe) {
        val current = _recipeList.value.orEmpty().toMutableList()
        current.add(recipe)
        _recipeList.value = current
    }

}

