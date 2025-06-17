package com.example.wooriga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiaryViewModel : ViewModel() {

    private val _diaryList = MutableLiveData<List<Diary>>()
    val diaryList: LiveData<List<Diary>> get() = _diaryList

    init {
        // 초기 더미 데이터
        _diaryList.value = listOf(
            Diary(
                date = "6월 15일 토요일",
                imageUri = null,
                title = "주말 나들이",
                location = "서울숲",
                content = "가족과 함께 서울숲 나들이를 다녀왔다. 날씨가 맑고 기분이 좋았다.",
                tag = listOf("#화목", "#산책"),
                member = listOf("@엄마", "@아빠")
            ),
            Diary(
                date = "6월 10일 월요일",
                imageUri = null,
                title = "학교 발표",
                location = "학교",
                content = "오늘은 학교에서 프로젝트 발표를 했다. 떨렸지만 잘 마무리했다.",
                tag = listOf("#성장", "#도전"),
                member = listOf("@나", "@친구")
            )
        )
    }

    fun addDiary(newDiary: Diary) {
        val currentList = _diaryList.value ?: emptyList()
        _diaryList.value = currentList + newDiary
    }
}
