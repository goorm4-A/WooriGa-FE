package com.example.wooriga.model

import com.google.gson.annotations.SerializedName

data class Anniversary(
    val anniversaryId: Long? = null,
    val familyId: Int,

    val date: String,
    var title: String,
    @SerializedName("type") var tag: String? = null,
    var location: String,
    @SerializedName("description") var memo: String
)

data class PageableRequest(
    val page: Int,
    val size: Int,
    val sort: List<String>
)