package com.example.wooriga.ui.familydiary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wooriga.model.Diary

class FamilyDiaryViewModel : ViewModel() {

    // MutableLiveData 내부 데이터를 숨기고 외부엔 LiveData만 노출
    private val _diaryList = MutableLiveData<List<Diary>>()
    val diaryList: LiveData<List<Diary>> get() = _diaryList

    init {
        // 예시 데이터 로딩
        loadSampleData()
    }

    private fun loadSampleData() {
        _diaryList.value = listOf(
            Diary(id = "1", imageUrl = "https://example.com/image1.jpg", preview = "롯데월드 갔다온 날^^"),
            Diary(id = "2", imageUrl = "https://example.com/image2.jpg", preview = "추석 가족 모임"),
            Diary(id = "3", imageUrl = "https://example.com/image3.jpg", preview = "강릉 여행 사진")
        )
    }
}
