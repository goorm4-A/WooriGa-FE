package com.example.wooriga

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
            }
        }
    }

    fun addMotto(userId: Long, familyName: String, motto: String) {
        viewModelScope.launch {
            repository.addMotto(userId, MottoRequest(familyName, motto))
            loadMottos(familyId = 1L, userId = userId)  // 새로고침
        }
    }

}
