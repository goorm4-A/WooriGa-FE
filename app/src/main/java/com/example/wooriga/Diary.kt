package com.example.wooriga

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Diary(
    val date: String,
    val imageUri: String?,
    val title: String,
    val location: String,
    val content: String,
    val tag: List<String>,
    val member: List<String>
) : Parcelable