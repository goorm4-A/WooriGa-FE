package com.example.wooriga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DiaryViewModel : ViewModel() {

    private val repository = DiaryRepository()

    private val _diaryList = MutableLiveData<List<DiaryListItem>>()
    val diaryList: LiveData<List<DiaryListItem>> get() = _diaryList

    fun loadDiaries() {
        viewModelScope.launch {
            val items = repository.fetchDiaryList(
                page = 0,
                size = 20,
                familyId = 123L  // TODO: 실제 familyId로 바꾸기
            )
            _diaryList.value = items ?: emptyList()
        }
    }

    init {
        // 초기 더미 데이터
//        _diaryList.value = listOf(
//            Diary(
//                date = "6월 15일 토요일",
//                imageUri = null,
//                title = "주말 나들이",
//                location = "서울숲",
//                content = "가족과 함께 서울숲 나들이를 다녀왔다. 날씨가 맑고 기분이 좋았다. good",
//                tag = listOf("#화목", "#산책"),
//                member = listOf("@엄마", "@아빠")
//            ),
//            Diary(
//                date = "6월 13일 월요일",
//                imageUri = null,
//                title = "학교 발표",
//                location = "학교",
//                content = "오늘은 학교에서 프로젝트 발표를 했다. 떨렸지만 잘 마무리했다.",
//                tag = listOf("#성장", "#도전", "#tag"),
//                member = listOf("@나", "@친구")
//            ),
//            Diary(
//                date = "6월 10일 월요일",
//                imageUri = null,
//                title = "test",
//                location = "학교",
//                content = "오늘은 학교에서 프로젝트 발표를 했다. 떨렸지만 잘 마무리했다. good",
//                tag = listOf("#성장", "#도전"),
//                member = listOf("@나", "@친구")
//            )
//        )
    }

//    fun addDiary(newDiary: Diary) {
//        val currentList = _diaryList.value ?: emptyList()
//        _diaryList.value = currentList + newDiary
//    }

    // 제목 + 내용 + 해시태그 기준 검색
//    fun searchDiary(query: String): List<Diary> {
//        val trimmedQuery = query.trim()
//        if (trimmedQuery.isEmpty()) return emptyList()
//
//        return _diaryList.value.orEmpty().filter {
//            it.title.contains(trimmedQuery, true) ||
//                    it.content.contains(trimmedQuery, true) ||
//                    it.tag.any { tag -> tag.contains(trimmedQuery, true) }
//        }
//    }


}
