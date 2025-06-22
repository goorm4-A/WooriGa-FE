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


    fun postMood(familyId: Long, moodType: String, tags: List<String>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                val tagString = tags.joinToString(",") { "#$it" }

                // 여기에 디버깅 로그 추가
                println("🔥 moodType: $moodType")
                println("🔥 tagString: $tagString")


                val request = MoodRequest(tags = tagString, moodType = moodType)

                val response = RetrofitClient.moodApi.postFamilyMood(familyId, request)
                if (response.isSuccess) {
                    println("MoodViewModel: 분위기 등록 성공 → ID: ${response.result.id}")
                    onSuccess()

                    // 등록 후 목록 갱신
                    loadFamilyMoods(familyId)
                } else {
                    println("MoodViewModel: 등록 실패 → ${response.code}, ${response.message}")
                    onFailure()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onFailure()
            }
        }
    }

}
