package com.example.wooriga

import android.util.Log
import com.example.wooriga.model.Anniversary

object AnniversaryRepository {

    private val anniversaryList = mutableListOf<Anniversary>()

    // API 연동

    // 서버에서 받아오기
    suspend fun fetchAnniversariesFromApi(
        type: String?,
        lastId: Long?,
        page: Int = 0,
        size: Int = 6,
        sort: List<String> = listOf("date,desc")
    ): AnniversaryResult? {
        val response = RetrofitClient2.annivApi.getAnniversaries(
            type = type,
            lastId = lastId,
            page = page,
            size = size,
            sort = sort
        )
        Log.d("AnnivAPI", "code=${response.code()}, body=${response.body()}, error=${response.errorBody()?.string()}")

        return if (response.isSuccessful && response.body()?.isSuccess == true) {
            response.body()?.result
        } else {
            null
        }
    }

    // 서버에 등록
    suspend fun addToApi(anniv: Anniversary): Boolean {
        val response = RetrofitClient2.annivApi.addAnniversary(anniv)
        if (response.isSuccessful) {
            anniversaryList.add(anniv)
        }
        return response.isSuccessful
    }

    // 태그 필터링된 리스트 반환 (tag가 ""이면 전체)
    fun getFiltered(tag: String): List<Anniversary> {
        return if (tag.isBlank()) {
            anniversaryList
        } else {
            anniversaryList.filter { it.tag == tag }
        }
    }

    // 전체 데이터 반환 (필요 시 사용 가능)
    fun getAll(): List<Anniversary> = anniversaryList

    // 항목 추가
    fun addAnniversary(anniv: Anniversary) {
        anniversaryList.add(anniv)
    }

/*    // 항목 수정
    fun updateAnniversary(anniv: Anniversary) {
        // 리스트에서 해당 기념일 찾아서 업데이트 (예: 날짜 + 제목으로 식별)
        val index =
            anniversaryList.indexOfFirst { it.date == anniv.date && it.title == anniv.title }
        if (index != -1) {
            anniversaryList[index] = anniv
        }
    }

    // 항목 삭제
    fun deleteAnniversary(anniv: Anniversary) {
        anniversaryList.removeIf { it.date == anniv.date && it.title == anniv.title }
    }*/
}