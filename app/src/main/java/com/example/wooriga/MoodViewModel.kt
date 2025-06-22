package com.example.wooriga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MoodViewModel : ViewModel() {

    // 내부에서 수정 가능한 MutableLiveData
    private val _moodList = MutableLiveData<List<Mood>>()
    val moodList: LiveData<List<Mood>> = _moodList // 외부에 노출되는 건 읽기 전용

    fun loadFamilyMoods(familyId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.moodApi.getFamilyMoods(familyId)
                if (response.isSuccess) {
                    val moods = response.result.map { moodRes ->
                        Mood(
                            id = moodRes.id,
                            familyId = familyId,
                            moodType = moodRes.moodType,
                            tags = moodRes.tags
                        )
                    }
                    _moodList.value = moods

                    // 성공 로그
                    println("MoodViewModel: 분위기 목록 불러오기 성공 (${moods.size}개)")
                } else {
                    // 실패 로그
                    println("MoodViewModel: 서버 오류 - ${response.code}, ${response.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("MoodViewModel: 네트워크 오류 - ${e.message}")
            }
        }
    }



//    init {
//        loadDummyData()
//    }
//
//    private fun loadDummyData() {
//        val dummyList = listOf(
//            Mood(1, "A가족", "행복", listOf("화목", "기쁨")),
//            Mood(2, "A가족", "감동", listOf("감사", "따뜻함")),
//            Mood(3, "A가족", "응원", listOf("자신감", "용기")),
//            Mood(4, "A가족", "슬픔", listOf("위로", "공감")),
//            Mood(5, "A가족", "화남", listOf("분노", "짜증"))
//        )
//        _moodList.value = dummyList
//    }

    // 분위기 추가
    fun addMood(mood: Mood) {
        val updatedList = _moodList.value.orEmpty().toMutableList().apply {
            add(0, mood) // 최근 등록 순
        }
        _moodList.value = updatedList
    }

}
