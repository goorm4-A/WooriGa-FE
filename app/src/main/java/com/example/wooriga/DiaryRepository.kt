package com.example.wooriga

class DiaryRepository {
    private val api = RetrofitClient.diaryApi

    suspend fun fetchDiaryList(
        page: Int,
        size: Int,
        familyId: Long,
        sort: List<String> = listOf("createdAt,desc"),
        lastDiaryId: Long? = null
    ): List<DiaryListItem>? {
        val response = api.getDiaryList(page, size, sort, familyId, lastDiaryId)
        return if (response.isSuccessful && response.body()?.isSuccess == true) {
            response.body()?.result?.contents
        } else null
    }

}

