package com.example.wooriga

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class MottoViewModel : ViewModel() {

    private val repository = MottoRepository()
    val mottos = MutableLiveData<List<Motto>>()

    fun loadMottos(familyId: Long, userId: Long) {
        viewModelScope.launch {
            val response = repository.getMottos(familyId, userId)
            if (response.isSuccessful) {
                mottos.value = response.body()?.result?.mottos ?: emptyList()
            } else {
                Log.e("MottoViewModel", "API 실패: ${response.code()}, ${response.errorBody()?.string()}")
            }
        }
    }

    fun addMotto(userId: Long, familyId: Long, familyName: String, motto: String) {
        viewModelScope.launch {
            try {
                val response = repository.addMotto(familyId, MottoRequest(familyName, motto)) // ✅ 수정
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d("MottoViewModel", "가훈 등록 성공")
                    loadMottos(familyId = familyId, userId = userId)
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

    fun editMotto(familyId: Long, mottoId: Long, userId: Long, familyName: String, motto: String) {
        viewModelScope.launch {
            try {
                val response = repository.updateMotto(familyId, mottoId, userId, MottoRequest(familyName, motto))
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

}
