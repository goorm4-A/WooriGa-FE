package com.example.wooriga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MoodViewModel : ViewModel() {

    // 내부에서 수정 가능한 MutableLiveData
    private val _moodList = MutableLiveData<List<Mood>>()
    val moodList: LiveData<List<Mood>> = _moodList // 외부에 노출되는 건 읽기 전용

    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        val dummyList = listOf(
            Mood(1, "A가족", "행복", listOf("화목", "기쁨")),
            Mood(2, "A가족", "감동", listOf("감사", "따뜻함")),
            Mood(3, "A가족", "응원", listOf("자신감", "용기")),
            Mood(4, "A가족", "슬픔", listOf("위로", "공감")),
            Mood(5, "A가족", "화남", listOf("분노", "짜증"))
        )
        _moodList.value = dummyList
    }

    // 분위기 추가
    fun addMood(mood: Mood) {
        val updatedList = _moodList.value.orEmpty().toMutableList().apply {
            add(0, mood) // 최근 등록 순
        }
        _moodList.value = updatedList
    }

}
