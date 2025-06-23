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
import com.example.wooriga.utils.ToolbarUtils
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

                // 💡 가족사 재조회 (예: 등록한 가족의 id를 알고 있어야 함)
                val familyId = getFamilyIdFromFamilyName(it.family)  // 이 함수 필요!
                getEvents(familyId)     // 타임라인용
                getEventsMap(familyId)  // 지도용
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
            result.onFailure {
                Log.e("HistoryViewModel", "지도용 가족사 조회 실패: ${it.message}")
            }
        }
    }

    private fun getFamilyIdFromFamilyName(name: String): Long {
        return ToolbarUtils.groupList.find {
            it.familyGroup.familyName == name
        }?.familyGroup?.familyGroupId ?: -1L
    }

    fun getAllFamilyMapEvents(groupList: List<FamilyGroupResponse>) {
        Log.d("지도", "getAllFamilyMapEvents 호출됨 - 그룹 수: ${groupList.size}")
        viewModelScope.launch {
            val deferreds = groupList.map { group ->

                async {
                    try {
                        val result = repository.getEventsMap(group.familyGroupId)
                        Log.d("지도", "API 호출 후 결과: $result")

                        result.map { list ->
                            Log.d("지도", "familyId: ${group.familyGroupId}, 이벤트 수: ${list.size}")
                            list.map { HistoryWithFamilyId(it, group.familyGroupId) }
                        }.getOrElse {
                            Log.e("지도", "실패: ${it.message}")
                            emptyList()
                        }
                    } catch (e: Exception) {
                        Log.e("지도", "예외 발생: ${e.message}")
                        emptyList()
                    }
                }
            }

            val results = deferreds.awaitAll().flatten()
            _allMapEvents.value = results
        }
    }

}