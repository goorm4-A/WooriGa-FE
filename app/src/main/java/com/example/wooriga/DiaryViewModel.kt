package com.example.wooriga

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wooriga.model.FamilyMember
import kotlinx.coroutines.launch

class DiaryViewModel : ViewModel() {

    private val repository = DiaryRepository()

    private val _diaryList = MutableLiveData<List<DiaryListItem>>()
    val diaryList: LiveData<List<DiaryListItem>> get() = _diaryList

    private val _selectedFamilyId = MutableLiveData<Long>()
    val selectedFamilyId: LiveData<Long> get() = _selectedFamilyId

    private val _memberList = MutableLiveData<List<FamilyMember>>()
    val memberList: LiveData<List<FamilyMember>> get() = _memberList

    private val selectedParticipantIds = mutableSetOf<Long>()

    fun loadFamilyMembers(familyId: Long) {
        // TODO: 추후 API 연동으로 교체
        _memberList.value = listOf(
            FamilyMember(
                familyMemberId = 1,
                familyMemberName = "엄마",
                familyMemberImage = null,
                relation = "어머니",
                birthDate = "1970-01-01",
                isUserAdded = true
            ),
            FamilyMember(
                familyMemberId = 2,
                familyMemberName = "아빠",
                familyMemberImage = null,
                relation = "아버지",
                birthDate = "1970-01-01",
                isUserAdded = true
            ),
            FamilyMember(
                familyMemberId = 3,
                familyMemberName = "나",
                familyMemberImage = null,
                relation = "자녀",
                birthDate = "2000-01-01",
                isUserAdded = true
            )
        )
    }

    private val _comments = MutableLiveData<List<DiaryComment>>()
    val comments: LiveData<List<DiaryComment>> get() = _comments

    private val _searchResult = MutableLiveData<List<DiaryListItem>>()
    val searchResult: LiveData<List<DiaryListItem>> get() = _searchResult

    // 현재 로그인된 사용자 정보
    val savedUser = UserManager.loadUserInfo()
    val currentUserName = savedUser?.name ?: "이름 없음"
    val currentUserProfile = savedUser?.image ?: ""

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
            )

            if (items != null) {
                if (items.isEmpty()) {
                    android.util.Log.d("DiaryViewModel", "✅ 일기 목록 불러오기 성공 (가족 ID: $familyId) - 항목 없음")
                } else {
                    android.util.Log.d("DiaryViewModel", "✅ 일기 목록 불러오기 성공 (가족 ID: $familyId), 개수: ${items.size}")
                }
            } else {
                android.util.Log.e("DiaryViewModel", "❌ 일기 목록 불러오기 실패 (가족 ID: $familyId)")
            }
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
        context: Context,
        participantIds: List<Long>
    ) {
        viewModelScope.launch {
            val dto = FamilyDiaryDto(
                familyId = _selectedFamilyId.value ?: error("선택된 가족 그룹 없음"),
                username = currentUserName,
                profile = currentUserProfile,
                title = title,
                location = location,
                description = description,
                diaryTags = tags,
                // TODO: 실제 가족 구성원 목록을 서버에서 받아온 뒤, 체크박스로 선택해 넘기는 구조로 구현
                participantIds = selectedParticipantIds.toList()
            )

            val success = repository.uploadDiary(dto, imageUri, context)
            if (success) {
                loadDiaries() // 등록 후 목록 다시 불러오기
            } else {
                Toast.makeText(context, "일기 등록 실패", Toast.LENGTH_SHORT).show()
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
