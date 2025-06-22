package com.example.wooriga.model

import com.google.gson.annotations.SerializedName

data class History( //Map Response
    val eventId: Long?,
    @SerializedName("familyName") val family: String,
    @SerializedName("date")val dateString: String,        // "2025.06.02" - 표시용
    val title: String,
    @SerializedName("location") val locationName: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class HistoryTimeline( //Timeline 표시용
    @SerializedName("familyName") val family: String?,
    @SerializedName("date")val dateString: String,        // "2025.06.02" - 표시용
    val title: String,
    @SerializedName("location") val locationName: String,
)

data class HistoryRequest( // post
    @SerializedName("familyName") val family: String,
    @SerializedName("date")val dateString: String,        // "2025.06.02" - 표시용
    val title: String,
    @SerializedName("location") val locationName: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class HistoryWithFamilyId( // 지도 표시용
    val history: History,
    val familyId: Long
)
