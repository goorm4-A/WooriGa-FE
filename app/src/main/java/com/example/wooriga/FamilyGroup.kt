package com.example.wooriga.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T
)

data class FamilyGroupWrapper(
    val familyGroup: FamilyGroupResponse,
    val familyMembers: List<FamilyMember>?,
    val totalCnt: Int?
)

data class FamilyGroupRequest(
    val image: String? = null,
    @SerializedName("name") val title: String,
)


// 응답용 데이터 클래스 (받을 때)
data class FamilyGroupResponse(
    val familyGroupId: Long,
    val familyName: String,
    val familyImage: String? = null,
    val inviteCode: Int
)

data class FamilyMember(
    val familyMemberId: Long,
    val familyMemberName: String?,
    val familyMemberImage: String? = null,
    val relation: String,
    val birthDate: String?,
    val isUserAdded: Boolean
)
