package com.example.wooriga

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class RecipeViewModel : ViewModel() {

    private val _recipeList = MutableLiveData<List<Recipe>>()
    val recipeList: LiveData<List<Recipe>> get() = _recipeList

    private val addedRecipeList = mutableListOf<Recipe>()

    private val repository = RecipeRepository()

    fun loadRecipes(familyId: Long, lastRecipeId: Long? = null) {
        viewModelScope.launch {
            try {
                val result = repository.fetchRecipes(familyId, lastRecipeId)
                Log.d("RecipeViewModel", "요리법 ${result.size}개 불러옴")

                // 로컬 데이터
                val dummy = getDummyRecipes()
                val merged = result + dummy + addedRecipeList
                _recipeList.value = merged

                //_recipeList.value = result
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "레시피 로딩 실패", e)
            }
        }
    }

    fun createRecipe(
        familyId: Long,
        recipe: Recipe,
        coverImages: List<Uri>?,
        stepImages: List<Uri>?,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val recipeJson = Gson().toJson(recipe)
                val recipeBody = recipeJson.toRequestBody("application/json".toMediaType())

                val coverParts = coverImages?.mapIndexed { i, uri ->
                    uri.toMultipart("coverImages", "cover_$i.jpg", context)
                }

                val stepParts = stepImages?.mapIndexed { i, uri ->
                    uri.toMultipart("stepImages", "step_$i.jpg", context)
                }

                val response = RetrofitClient.recipeApi.createRecipe(
                    familyId,
                    recipeBody,
                    coverParts,
                    stepParts
                )

                if (response.isSuccess) {
                    onSuccess()
                } else {
                    onFailure(response.message ?: "등록 실패")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                onFailure("오류 발생: ${e.message}")
            }
        }
    }

    fun Uri.toMultipart(partName: String, fileName: String, context: Context): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(this) ?: error("파일 열기 실패")
        val bytes = inputStream.readBytes()
        val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, fileName, requestBody)
    }

    private fun getDummyRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = "1",
                title = "쉬운 김치볶음밥 만드는 법 알려줄게",
                author = "김숙명",
                description = "매콤하고 맛있는 한끼",
                cookTimeMinutes = 60,
                coverImageUrl = "https://img.cjthemarket.com/images/file/product/833/20240625112013818.jpg?SF=webp",
                ingredients = listOf("밥 1공기", "김치 1/2컵", "대파 1/2대", "참기름 1큰술", "고추장 1작은술"),
                steps = listOf(
                    CookingStep(1, "팬에 참기름을 두르고 대파를 볶아 향을 낸다."),
                    CookingStep(2, "김치를 넣고 중불에서 3분간 볶는다."),
                    CookingStep(3, "밥을 넣고 고추장과 함께 골고루 볶는다."),
                    CookingStep(4, "그릇에 담고 계란 프라이와 함께 낸다.")
                )
            ),
            Recipe(
                id = "2",
                title = "양념 두부 조림",
                author = "김숙명",
                description = "두부에 간이 쏙쏙~",
                cookTimeMinutes = 45,
                coverImageUrl = "https://static.wtable.co.kr/image/production/service/recipe/2179/12839e7c-a432-44d1-b0f6-cbcd63b9248b.jpg?size=800x800",
                ingredients = listOf("두부 1모", "간장 3큰술", "고춧가루 1큰술", "다진 마늘 1작은술", "물 1/2컵"),
                steps = listOf(
                    CookingStep(1, "두부를 적당한 크기로 썬 후 기름에 앞뒤로 노릇하게 굽는다."),
                    CookingStep(2, "양념장 재료(간장, 고춧가루, 마늘, 물)를 섞는다."),
                    CookingStep(3, "구운 두부에 양념장을 부어 중불에서 졸인다."),
                    CookingStep(4, "양념이 반쯤 졸아들면 불을 끈다.")
                )
            )
        )
    }


    fun addRecipe(recipe: Recipe) {
        addedRecipeList.add(recipe)

        // 기존 목록과 새 레시피를 합쳐 다시 보여줌
        val current = _recipeList.value.orEmpty()
        _recipeList.value = current + recipe
    }

}

