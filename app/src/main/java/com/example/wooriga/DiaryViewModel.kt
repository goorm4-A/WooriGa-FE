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

    private val _familyList = MutableLiveData<List<Family>>()
    val familyList: LiveData<List<Family>> get() = _familyList

    private val _selectedFamilyId = MutableLiveData<Long>()

    private val _comments = MutableLiveData<List<DiaryComment>>()
    val comments: LiveData<List<DiaryComment>> get() = _comments

    private val _searchResult = MutableLiveData<List<DiaryListItem>>()
    val searchResult: LiveData<List<DiaryListItem>> get() = _searchResult

    fun loadFamilies() {
        val dummy = listOf(
            Family(1, "A가족"),
            Family(2, "B가족"),
            Family(3, "C가족")
        )
        _familyList.value = dummy
        _selectedFamilyId.value = dummy.first().familyId
        loadDiaries(dummy.first().familyId)
    }

    // 가족 선택
    fun selectFamily(familyId: Long) {
        _selectedFamilyId.value = familyId
        loadDiaries(familyId)
    }

    // 일기 목록 조회

    // 오버로딩
    // familyId를 내부에서 가져와서 호출
    fun loadDiaries() {
        val currentFamilyId = _selectedFamilyId.value ?: return
        loadDiaries(currentFamilyId)
    }

    // familyId를 외부에서 받아서 호출
    fun loadDiaries(familyId: Long) {
        viewModelScope.launch {
            val items = repository.fetchDiaryList(
                page = 0,
                size = 20,
                familyId = familyId
            ) ?: emptyList()

            // 리스트 맨 위에 더미 추가
            val dummy = DiaryListItem(
                id = -1L,
                imgUrl = "https://ik.imagekit.io/tvlk/blog/2024/10/shutterstock_2479862045.jpg?tr=q-70,c-at_max,w-500,h-250,dpr-2",
                title = "테스트 일기입니다."
            )

            _diaryList.value = listOf(dummy) + items
        }
    }

    // 댓글 조회
    fun loadComments(diaryId: Long) {
        viewModelScope.launch {
            val result = repository.fetchDiaryComments(diaryId)
            _comments.value = result ?: emptyList()
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

    // 일기 삭제
    fun deleteDiary(diaryId: Long, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteDiary(diaryId)
            if (result?.isSuccess == true) {
                onSuccess()
                loadDiaries() // 삭제 후 목록 다시 불러오기
            } else {
                onFailure()
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
