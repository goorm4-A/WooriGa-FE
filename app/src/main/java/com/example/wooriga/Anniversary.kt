package com.example.wooriga.model

import com.google.gson.annotations.SerializedName

data class Anniversary(
    val anniversaryId: Int? = null,
    val familyId: Int,

    val date: String,
    var title: String,
    @SerializedName("type") var tag: String,
    var location: String,
    @SerializedName("description") var memo: String
)