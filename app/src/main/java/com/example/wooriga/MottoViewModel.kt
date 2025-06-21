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
            Motto(1L, "착하게 살자", 1L, "A가족", "2025-06-04"),
            Motto(2L, "정직하게 살자", 2L, "A가족", "2025-06-05"),
            Motto(3L, "배려하며 살자", 3L, "A가족", "2025-06-06")
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
            familyId = -1L,
            familyName = familyName,
            createdAt = getToday()
        )
        mottos.value = currentList + newMotto

        viewModelScope.launch {
            try {
                val response = repository.addMotto(userId, MottoRequest(familyName, motto))
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d("MottoViewModel", "가훈 등록 성공")
                    // 등록 후 새로 불러오기
                    loadMottos(familyId = 1L, userId = userId) // TODO: 실제 familyId로 교체
                } else {
                    Log.e("MottoViewModel", "가훈 등록 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MottoViewModel", "네트워크 오류: ${e.localizedMessage}")
            }
        }
    }

    fun deleteMotto(mottoId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.deleteMotto(mottoId, userId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    // 삭제 성공 시 목록 갱신 또는 UI 업데이트
                    mottos.value = mottos.value?.filter { it.id != mottoId }
                    Log.d("MottoViewModel", "삭제 성공")
                } else {
                    Log.e("MottoViewModel", "삭제 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MottoViewModel", "네트워크 오류: ${e.localizedMessage}")
            }
        }
    }

    fun editMotto(mottoId: Long, userId: Long, familyName: String, motto: String) {
        viewModelScope.launch {
            try {
                val response = repository.updateMotto(mottoId, userId, MottoRequest(familyName, motto))
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val updated = response.body()!!.result
                    mottos.value = mottos.value?.map {
                        if (it.id == updated.id) updated else it
                    }
                    Log.d("MottoViewModel", "수정 성공: ${updated.title}")
                } else {
                    Log.e("MottoViewModel", "수정 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MottoViewModel", "수정 오류: ${e.localizedMessage}")
            }
        }
    }


    // 오늘 날짜 불러오는 함수
    private fun getToday(): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date())
    }

}
