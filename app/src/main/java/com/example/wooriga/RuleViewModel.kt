package com.example.wooriga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RuleViewModel : ViewModel() {
    private val _ruleList = MutableLiveData<List<Rule>>()
    val ruleList: LiveData<List<Rule>> = _ruleList

    init {
        _ruleList.value = listOf(
            Rule("A가족", "필수 규칙", "착하게 살자", "서로 예의를 지키자", "2025년 4월 12일 토요일"),
            Rule("A가족", "권장 사항", "일찍 일어나기", "건강한 생활 습관", "2025년 4월 13일 일요일"),
            Rule("A가족", "권장 사항", "아침밥 챙겨먹기", "건강한 생활 습관", "2025년 4월 14일 월요일"),
            Rule("B가족", "금기 사항", "욕하지 않기", "존중하는 말 사용", "2025년 4월 11일 금요일")
        )
    }

    fun addRule(rule: Rule) {
        _ruleList.value = _ruleList.value.orEmpty() + rule
    }
}