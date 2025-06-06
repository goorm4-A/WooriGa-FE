package com.example.wooriga

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MottoViewModel : ViewModel() {

    private val repository = MottoRepository()
    val mottos = MutableLiveData<List<Motto>>()

    fun loadMottos(familyId: Long, userId: Long) {

        // test
        // 더미 데이터로 리스트 채우기
        val dummyList = listOf(
            Motto(1L, "착하게 살자", "A가족", "2025-06-04"),
            Motto(2L, "정직하게 살자", "A가족", "2025-06-05"),
            Motto(3L, "배려하며 살자", "A가족", "2025-06-06")
        )
        mottos.value = dummyList

        viewModelScope.launch {
            val response = repository.getMottos(familyId, userId)
            if (response.isSuccessful) {
                mottos.value = response.body()?.result?.mottos ?: emptyList()
            } else {
                Log.e("MottoViewModel", "API 실패: ${response.code()}")
            }
        }
    }

    fun addMotto(userId: Long, familyName: String, motto: String) {

        // test
        val currentList = mottos.value.orEmpty()
        val newId = (currentList.maxOfOrNull { it.id } ?: 0L) + 1
        val newMotto = Motto(
            id = newId,
            title = motto,
            familyName = familyName,
            createdAt = getToday()
        )
        mottos.value = currentList + newMotto

        // api 사용
//        viewModelScope.launch {
//            repository.addMotto(userId, MottoRequest(familyName, motto))
//            loadMottos(familyId = 1L, userId = userId)  // 새로고침
//        }
    }

    fun editMotto(mottoId: Long, newFamily: String, newTitle: String) {
        val currentList = mottos.value.orEmpty()
        val updatedList = currentList.map {
            if (it.id == mottoId) it.copy(title = newTitle, familyName = newFamily) else it
        }
        mottos.value = updatedList

        // api 사용
    }


    // 오늘 날짜 불러오는 함수
    private fun getToday(): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date())
    }

}
