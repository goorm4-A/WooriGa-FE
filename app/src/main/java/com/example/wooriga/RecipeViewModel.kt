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
                ingredients = emptyList(),
                steps = emptyList()
            ),
            Recipe(
                id = "2",
                title = "양념 두부 조림",
                author = "김숙명",
                description = "두부에 간이 쏙쏙~",
                cookTimeMinutes = 45,
                coverImageUrl = null,
                ingredients = emptyList(),
                steps = emptyList()
            )
        )
        _recipeList.value = dummyList
    }
}

