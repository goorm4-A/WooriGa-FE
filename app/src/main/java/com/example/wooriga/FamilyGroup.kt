package com.example.wooriga.model

import com.google.gson.annotations.SerializedName

data class FamilyGroup(
    val groupId: Long?,
    val image: String? = null,
    @SerializedName("name") val title: String,
    val memberCount: Int?
)
// title -> 가족 그룹 이름