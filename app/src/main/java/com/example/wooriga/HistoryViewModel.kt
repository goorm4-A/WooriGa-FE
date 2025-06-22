package com.example.wooriga

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wooriga.model.FamilyGroupResponse
import com.example.wooriga.model.History
import com.example.wooriga.model.HistoryRequest
import com.example.wooriga.model.HistoryWithFamilyId
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: HistoryRepository) : ViewModel() {

    private val _historyList = MutableLiveData<List<History>>()
    val historyList: LiveData<List<History>> get() = _historyList

    private val _allMapEvents = MutableLiveData<List<HistoryWithFamilyId>>()
    val allMapEvents: LiveData<List<HistoryWithFamilyId>> get() = _allMapEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 가족사 등록
    fun createEvent(
        request: HistoryRequest,
        onSuccess: (History) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.createEvent(request)
            result.onSuccess {
                onSuccess(it)
            }.onFailure {
                onError(it.message ?: "오류 발생")
            }
        }
    }

    // 타임라인 가족사 목록 조회
    fun getEvents(familyId: Long) {
        viewModelScope.launch {
            val result = repository.getEvents(familyId)
            result.onSuccess { list ->
                _historyList.value = list
            }.onFailure { error ->
                Log.e("HistoryViewModel", "가족사 조회 실패: ${error.message}")
            }
        }
    }


    // 지도
    fun getEventsMap(familyId: Long) {
        viewModelScope.launch {
            val result = repository.getEventsMap(familyId)
            result.onSuccess { list ->
                _historyList.value = list
            }.onFailure { error ->
                Log.e("HistoryViewModel", "가족사 조회 실패: ${error.message}")
            }
        }
    }

    //
    fun getAllFamilyMapEvents(groupList: List<FamilyGroupResponse>) {
        viewModelScope.launch {
            val deferreds = groupList.map { group ->
                async {
                    val result = repository.getEventsMap(group.familyGroupId)
                    result.map { list ->
                        list.map { HistoryWithFamilyId(it, group.familyGroupId) }
                    }.getOrElse { emptyList() }
                }
            }
            val results = deferreds.awaitAll().flatten()
            _allMapEvents.value = results
        }
    }
    /*
    fun getAllFamilyMapEvents(groupList: List<FamilyGroupResponse>) {
        viewModelScope.launch {
            val allEvents = mutableListOf<HistoryWithFamilyId>()

            for (group in groupList) {
                val result = repository.getEventsMap(group.familyGroupId)
                result.onSuccess { list ->
                    val mapped = list.map { history ->
                        HistoryWithFamilyId(history, group.familyGroupId)
                    }
                    allEvents.addAll(mapped)
                }.onFailure {
                    Log.e("ViewModel", "가족사 조회 실패: ${it.message}")
                }
            }

            _allMapEvents.value = allEvents
        }
    }

     */

}