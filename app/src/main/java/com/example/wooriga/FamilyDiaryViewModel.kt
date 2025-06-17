package com.example.wooriga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FamilyDiaryViewModel : ViewModel() {

    // MutableLiveData 내부 데이터를 숨기고 외부엔 LiveData만 노출
    private val _diaryList = MutableLiveData<List<Diary>>()
    val diaryList: LiveData<List<Diary>> get() = _diaryList

    fun addDiary(newDiary: Diary) {
        val currentList = _diaryList.value.orEmpty().toMutableList()
        currentList.add(0, newDiary) // 최신 항목 상단 삽입
        _diaryList.value = currentList
    }

//    init {
//        // 예시 데이터 로딩
//        loadSampleData()
//    }
//
//    private fun loadSampleData() {
//        _diaryList.value = listOf(
//            Diary(imageUri = "https://example.com/image1.jpg", title = "롯데월드 갔다온 날^^"),
//            Diary(imageUri = "https://example.com/image2.jpg", title = "추석 가족 모임"),
//            Diary(imageUri = "https://example.com/image3.jpg", title = "강릉 여행 사진")
//        )
//    }
}
