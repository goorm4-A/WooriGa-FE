package com.example.wooriga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RuleViewModel : ViewModel() {

    private val _ruleList = MutableLiveData<List<Rule>>()
    val ruleList: LiveData<List<Rule>> = _ruleList

    init {
        // 초기 규칙 데이터
        _ruleList.value = listOf(
            Rule("A가족", "필수 규칙", "착하게 살자.", "서로 예의를 지키자", "2025년 4월 12일 토요일"),
            Rule("A가족", "권장 사항", "일찍 일어나기", "건강한 생활 습관", "2025년 4월 13일 일요일"),
            Rule("B가족", "금기 사항", "욕하지 않기", "언제나 존중하는 말 사용", "2025년 4월 11일 금요일")
        )
    }

    fun addRule(rule: Rule) {
        val current = _ruleList.value.orEmpty().toMutableList()
        current.add(rule)
        _ruleList.value = current
    }
}
