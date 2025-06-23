package com.example.wooriga

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wooriga.utils.ToolbarUtils.currentGroup
import kotlinx.coroutines.launch

class RuleViewModel : ViewModel() {

    private val _ruleList = MutableLiveData<List<Rule>>()
    val ruleList: LiveData<List<Rule>> = _ruleList

    private val repository = RuleRepository()

    val savedUser = UserManager.loadUserInfo()
    val userId = savedUser?.userId
    val familyId = currentGroup?.familyGroup?.familyGroupId

    fun loadRules(familyId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                Log.d("RuleViewModel", "요청: familyId=$familyId, userId=$userId")

                val response = RetrofitClient.ruleApi.getRules(familyId, userId)

                Log.d("RuleViewModel", "응답 성공 여부: ${response.isSuccess}, 코드: ${response.code}")

                if (response.isSuccess) {
                    val rules = response.result?.rules.orEmpty()
                        .map { it.toUiModel() }

                    _ruleList.value = rules
                } else {
                    Log.e("RuleViewModel", "응답 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("RuleViewModel", "서버 오류: ${e.message}", e)
            }
        }
    }

    fun addRuleRemote(request: RuleRequest) {
        viewModelScope.launch {
            try {
                val response = repository.addRule(request)
                if (response.isSuccess) {
                    Log.d("RuleViewModel", "약속 생성 성공")

                    // 서버 응답 후 목록 새로고침
                    // 필요한 정보: familyId, userId

                    if (userId != null && familyId != null) {
                        loadRules(familyId, userId)
                    }
                } else {
                    Log.e("RuleViewModel", "약속 생성 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("RuleViewModel", "서버 오류: ${e.message}")
            }
        }
    }

    fun deleteRule(ruleId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.deleteRule(ruleId)
                if (response.isSuccess) {
                    Log.d("RuleViewModel", "삭제 성공: ruleId=$ruleId")

                    // 목록 갱신
                    if (userId != null && familyId != null) {
                        loadRules(familyId, userId)
                    }
                } else {
                    Log.e("RuleViewModel", "삭제 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("RuleViewModel", "서버 오류: ${e.message}")
            }
        }
    }

    fun updateRule(ruleId: Long, request: RuleRequest, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.updateRule(ruleId, request)
                if (response.isSuccess) {
                    onSuccess()
                    // 수정 후 리스트 갱신
                    if (userId != null && familyId != null) {
                        loadRules(familyId, userId)
                    }
                } else {
                    onFailure()
                }
            } catch (e: Exception) {
                Log.e("RuleViewModel", "규칙 수정 실패: ${e.message}", e)
                onFailure()
            }
        }
    }


}