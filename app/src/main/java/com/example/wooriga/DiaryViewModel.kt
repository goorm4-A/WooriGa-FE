package com.example.wooriga

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DiaryViewModel : ViewModel() {

    private val repository = DiaryRepository()

    private val _diaryList = MutableLiveData<List<DiaryListItem>>()
    val diaryList: LiveData<List<DiaryListItem>> get() = _diaryList

    private val _searchResult = MutableLiveData<List<DiaryListItem>>()
    val searchResult: LiveData<List<DiaryListItem>> get() = _searchResult

    // 일기 목록 조회
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

    // 일기 등록
    fun postDiary(
        title: String,
        location: String,
        description: String,
        tags: List<String>,
        imageUri: Uri?,
        context: Context
    ) {
        viewModelScope.launch {
            val dto = FamilyDiaryDto(
                title = title,
                location = location,
                description = description,
                diaryTags = tags,
                participantIds = listOf(1L, 2L) // 참여자 ID: 추후 실제 값으로 교체
            )

            val success = repository.uploadDiary(dto, imageUri, context)
            if (success) {
                loadDiaries() // 등록 후 목록 다시 불러오기
            } else {
                // 실패 처리
            }
        }
    }

    // 일기 검색
    fun searchDiaries(keyword: String, familyId: Long = 123L) {
        viewModelScope.launch {
            val results = repository.searchDiaryList(
                familyId = familyId,
                keyword = keyword
            )
            _searchResult.value = results ?: emptyList()
        }
    }

}
