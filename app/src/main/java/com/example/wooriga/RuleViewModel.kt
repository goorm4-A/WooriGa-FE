package com.example.wooriga

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RuleViewModel : ViewModel() {
    private val _ruleList = MutableLiveData<List<Rule>>()
    val ruleList: LiveData<List<Rule>> = _ruleList

    init {
        _ruleList.value = listOf(
            Rule("A가족", "필수 규칙", "착하게 살자", "서로 예의를 지키자", "2025-04-12"),
            Rule("A가족", "권장 사항", "일찍 일어나기", "건강한 생활 습관", "2025-04-13"),
            Rule("A가족", "권장 사항", "아침밥 챙겨먹기", "건강한 생활 습관", "2025-04-14"),
            Rule("B가족", "금기 사항", "욕하지 않기", "존중하는 말 사용", "2025-04-11")
        )
    }

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


    fun addRule(rule: Rule) {
        _ruleList.value = _ruleList.value.orEmpty() + rule
    }
}