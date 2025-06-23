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

    private val repository = RecipeRepository()

    fun loadRecipes(familyId: Long, lastRecipeId: Long? = null) {
        viewModelScope.launch {
            try {
                val result = repository.fetchRecipes(familyId, lastRecipeId)
                Log.d("RecipeViewModel", "요리법 ${result.size}개 불러옴")
                _recipeList.value = result
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "레시피 로딩 실패", e)
            }
        }
    }

    fun createRecipe(
        familyId: Long,
        recipeRequest: RecipeRequest,
        coverImageUri: Uri?, // 단일 대표 이미지
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val recipeJson = Gson().toJson(recipeRequest)
                val recipeBody = recipeJson.toRequestBody("application/json".toMediaType())

                val coverParts = coverImageUri?.let { uri ->
                    listOf(uri.toMultipart("coverImages", "cover.jpg", context))
                }

                val response = RetrofitClient.recipeApi.createRecipe(
                    familyId = familyId,
                    recipeJson = recipeBody,
                    coverImages = coverParts,
                    stepImages = null // 조리 이미지 추후 구현
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

    fun loadRecipeDetail(
        familyId: Long,
        recipeId: Long,
        onSuccess: (Recipe) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.recipeApi.getRecipeDetail(familyId, recipeId)
                if (response.isSuccess) {
                    val detail = response.result.toUiModel(recipeId)
                    onSuccess(detail)
                } else {
                    onFailure(response.message ?: "불러오기 실패")
                }
            } catch (e: Exception) {
                onFailure("네트워크 오류: ${e.message}")
            }
        }
    }

    fun deleteRecipe(
        familyId: Long,
        recipeId: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.recipeApi.deleteRecipe(familyId, recipeId)
                if (response.isSuccess) {
                    onSuccess()
                } else {
                    onFailure(response.message ?: "삭제 실패")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onFailure("오류: ${e.message}")
            }
        }
    }

}
